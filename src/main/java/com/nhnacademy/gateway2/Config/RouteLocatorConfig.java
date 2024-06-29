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
                        .and().method("GET", "DELETE", "PUT")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://CLIENT"))
                .route("client", p -> p.path("/api/client/address")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://CLIENT"))
                .route("client", p -> p.path("/api/client/phone")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://CLIENT"))
                .route("PRODUCT-SERVICE", p -> p.path("/api/product/admin/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://PRODUCT-SERVICE"))
                .route("PRODUCT-SERVICE", p -> p.path("/api/product/client/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://PRODUCT-SERVICE"))
                .route("ADMIN-SERVICE2", p -> p.path("/api/product/**")
                        .uri("lb://PRODUCT-SERVICE"))
                .route("TAG-SERVICE", p -> p.path("/api/product/**")
                        .uri("lb://PRODUCT-SERVICE"))

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
