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
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://PRODUCT-SERVICE"))

                // TODO order 쪽에서 product, coupon 추가한 내용. 충돌나면 삭제!
                .route("product", p -> p.path("/api/product/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://PRODUCT-SERVICE"))
                .route("coupon", p -> p.path("/api/coupon/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://COUPON"))

                .route("order", p -> p.path("/api/client/orders/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://orderPaymentRefund"))
                .route("order", p -> p.path("/api/non-client/orders/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://orderPaymentRefund"))
                .route("order", p -> p.path("/api/order/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://orderPaymentRefund"))
                .route("shipping", p -> p.path("/api/shipping-policy/**")
                        .and().query("type")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://orderPaymentRefund"))
            .route("coupon", p -> p.path("/api/coupon/myPage")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://COUPON"))
            .route("coupon", p -> p.path("/api/coupon/register/{couponPolicyId}")
                .and().method("POST")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))

                .uri("lb://COUPON"))
            .route("coupon", p -> p.path("/api/coupon/update")
                .uri("lb://COUPON"))
            .route("coupon", p -> p.path("/api/coupon/policy/**")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://COUPON"))
            .route("coupon", p -> p.path("/api/coupon/policy/register")
                .uri("lb://COUPON"))
            .route("coupon", p -> p.path("/api/coupon/type")
                .and().method("GET")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://COUPON"))
            .route("couponClient", p -> p.path("/api/client/coupon-payment")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://Client"))
            .route("COUPON", p -> p.path("/api/coupon/adminPage")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://COUPON"))
            .route("pointAccumulation", p -> p.path("/api/point/myPage/reward")
                .and().method("GET")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://POINT"))
            .route("pointAccumulation", p -> p.path("/api/point/adminPage/reward")
                .and().method("GET")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://POINT"))
            .route("pointPolicy", p -> p.path("/api/point/policy/register")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://POINT"))
            .route("pointPolicy", p -> p.path("/api/point/policy")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://POINT"))
            .route("pointPolicy", p -> p.path("/api/point/policy/{pointPolicyId}")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://POINT"))
            .route("pointUsage", p -> p.path("/api/point/myPage/use")
                .and().method("GET")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://POINT"))
            .route("pointUsage", p -> p.path("/api/point/adminPage/use")
                .and().method("GET")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://POINT"))
            .route("pointOrder", p -> p.path("/api/point/use/payment")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://POINT"))
            .route("pointOrder", p -> p.path("/api/point")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://POINT"))
            .route("pointOrder", p -> p.path("/api/point/order")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://POINT"))
                .route("search", p -> p.path("/api/search")
                        .and().method("GET")
                        .uri("lb://SEARCH"))

                .route("client", p -> p.path("/api/client/role", "/api/client/privacy-page")
                        .and().method("GET")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri("lb://CLIENT"))
                .route("client", p -> p.path("/api/client/grade")
                        .and().method("PUT")
                        .uri("lb://CLIENT"))

            .route("Point", p -> p.path("/api/client/name")
                .and().method("GET")
                .uri("lb://Client"))
            .route("pointAccumulation", p -> p.path("/api/point/adminPage/delete/{pointAccumulationHistoryId}")
                .and().method("DELETE")
                .uri("lb://POINT"))
            .route("birthdayUser", p -> p.path("/api/client/birth-coupon")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://Client"))
            .route("paymentReward", p -> p.path("/api/coupon/payment/reward")
                .filters(f -> f.filter(
                    jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                .uri("lb://COUPON"))
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
