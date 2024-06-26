package com.nhnacademy.gateway2.Config;

import com.nhnacademy.gateway2.filter.JwtAuthorizationHeaderFilter;
import feign.codec.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;

@RequiredArgsConstructor
@Configuration
public class RouteLocatorConfig {
    private final JwtAuthorizationHeaderFilter jwtAuthorizationHeaderFilter;

    @Bean
    public RouteLocator myRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth", p -> p.path("/login", "/logout", "/reissue")
                        .uri("lb://AUTH"))
                .route("client", p -> p.path("/api/client/login")
                        .uri("lb://CLIENT"))
                .route("client", p -> p.path("/api/client")
                        .and().method("POST")
                        .uri("lb://CLIENT"))
                .route("client", p -> p.path("/api/client")
                        .and().method("GET", "DELETE")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://CLIENT"))
                .route("client", p -> p.path("/api/client/address")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://CLIENT"))
                .route("client", p -> p.path("/api/client/phone")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://CLIENT"))
                .build();
    }

    @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return ServerCodecConfigurer.create();
    }

    @Bean
    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters();
    }

    @Bean
    public Encoder feignEncoder(HttpMessageConverters messageConverters) {
        return new SpringEncoder(() -> messageConverters);
    }
}
