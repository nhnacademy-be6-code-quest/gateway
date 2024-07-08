package com.nhnacademy.gateway2.Config;

import com.nhnacademy.gateway2.filter.JwtAuthorizationHeaderFilter;
import com.nhnacademy.gateway2.filter.SendUserIdOfHeaderFilter;
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

    private final SendUserIdOfHeaderFilter sendUserIdOfHeaderFilter;

    @Bean
    public RouteLocator myRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth", p -> p.path("/api/login", "/api/logout", "/api/reissue", "/api/payco/login/callback", "/api/oauth", "/api/payco/recovery/callback")
                        .uri("lb://AUTH"))
                .route("client", p -> p.path("/api/client/login", "/api/client/change-password", "/api/client/recovery-account", "/api/client/recovery-oauth-account")
                        .uri("lb://CLIENT"))
                .route("client", p -> p.path("/api/client", "/api/oauth/client")
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

                .route("review", p -> p.path("/api/image")
                    .and().method("POST")
                    //.filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                    .uri("lb://REVIEW"))

                .route("review", p -> p.path("/photo-reviews")
                    .and().method("POST", "GET")
                    .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                    .uri("lb://REVIEW"))
                .route("review", p -> p.path("/photo-reviews/{id}")
                    .and().method("GET", "PUT", "DELETE")
                    .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                    .uri("lb://REVIEW"))
                .route("review", p -> p.path("/photo-reviews/client")
                    .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                    .uri("lb://REVIEW"))
                .route("review", p -> p.path("/photo-reviews/product/{productId}")
                    .uri("lb://REVIEW"))
                .route("review", p -> p.path("/photo-reviews/has-written/{orderDetailId}")
                    .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                    .uri("lb://REVIEW"))
                .route("message", p -> p.path("/send/change-password", "/send/recover-account")
                    .uri("lb://MESSAGE"))
                .route("product", p -> p.path("/api/product/admin/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://PRODUCT-SERVICE"))
                .route("product", p -> p.path("/api/product/client/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://PRODUCT-SERVICE"))
                .route("product", p -> p.path("/api/product/**")
                        .filters(f -> f.filter(sendUserIdOfHeaderFilter.apply(new SendUserIdOfHeaderFilter.Config())))
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
