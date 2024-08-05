package com.nhnacademy.gateway2.config;

import com.nhnacademy.gateway2.filter.JwtAuthorizationHeaderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RouteLocatorConfig {
    private static final String AUTH = "auth";
    private static final String AUTH_LB = "lb://AUTH";

    private static final String CLIENT = "client";
    private static final String CLIENT_LB = "lb://CLIENT";

    private static final String REVIEW = "review";
    private static final String REVIEW_LB = "lb://REVIEW";

    private static final String MESSAGE = "message";
    private static final String MESSAGE_LB = "lb://MESSAGE";

    private static final String PRODUCT = "product";
    private static final String PRODUCT_LB = "lb://PRODUCT-SERVICE";

    private static final String POINT_ACCUMULATION = "pointAccumulation";
    private static final String POINT_POLICY = "pointPolicy";
    private static final String POINT_ORDER = "pointOrder";
    private static final String POINT_LB = "lb://POINT";

    private static final String COUPON = "coupon";
    private static final String COUPON_LB = "lb://COUPON";

    private static final String ORDER = "order";
    private static final String ORDER_LB = "lb://orderPaymentRefund";

    private final JwtAuthorizationHeaderFilter jwtAuthorizationHeaderFilter;

    @Bean
    public RouteLocator myRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(AUTH, p -> p.path("/api/login", "/api/logout", "/api/reissue", "/api/payco/login/callback", "/api/oauth", "/api/payco/recovery/callback")
                        .uri(AUTH_LB))
                .route(CLIENT, p -> p.path("/api/client/login", "/api/client/change-password", "/api/client/recovery-account", "/api/client/recovery-oauth-account")
                        .uri(CLIENT_LB))
                .route(CLIENT, p -> p.path("/api/client", "/api/oauth/client")
                        .and().method("POST")
                        .uri(CLIENT_LB))
                .route(CLIENT, p -> p.path("/api/client")
                        .and().method("GET", "DELETE", "PUT")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(CLIENT_LB))
                .route(CLIENT, p -> p.path("/api/client/address")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(CLIENT_LB))
                .route(CLIENT, p -> p.path("/api/client/phone")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(CLIENT_LB))

                .route(REVIEW, p -> p.path("/api/image")
                        .and().method("POST")
                        .uri(REVIEW_LB))

                .route(REVIEW, p -> p.path("/api/review", "/api/review/**", "/api/reviews/my")
                        .and().method("POST", "GET", "PUT")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(REVIEW_LB))
                .route(REVIEW, p -> p.path("/api/review/score", "/api/reviews/product")
                        .and().method("GET")
                        .uri(REVIEW_LB))

                .route(MESSAGE, p -> p.path("/send/change-password", "/send/recover-account/**")
                        .uri(MESSAGE_LB))

                .route(PRODUCT, p -> p.path("/api/product/admin/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(PRODUCT_LB))
                .route(PRODUCT, p -> p.path("/api/product/client/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(PRODUCT_LB))
                .route(PRODUCT, p -> p.path("/api/product/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(PRODUCT_LB))
                .route(PRODUCT, p -> p.path("/api/product/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(PRODUCT_LB))

                .route(COUPON, p -> p.path("/api/coupon/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(COUPON_LB))

                .route(ORDER, p -> p.path("/api/payment/method")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(ORDER_LB))
                .route(ORDER, p -> p.path("/api/payment/grade/**")
                        .uri(ORDER_LB))
                .route(ORDER, p -> p.path("/api/client/orders/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(ORDER_LB))
                .route(ORDER, p -> p.path("/api/non-client/orders/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(ORDER_LB))
                .route(ORDER, p -> p.path("/api/order/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(ORDER_LB))
                .route("shipping", p -> p.path("/api/shipping-policy/**")
                        .and().query("type")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(ORDER_LB))
                .route("shipping", p -> p.path("/api/shipping-policy/**")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(ORDER_LB))
                .route(COUPON, p -> p.path("/api/coupon/myPage")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(COUPON_LB))
                .route(COUPON, p -> p.path("/api/coupon/register/{couponPolicyId}")
                        .and().method("POST")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))

                        .uri(COUPON_LB))
                .route(COUPON, p -> p.path("/api/coupon/update")
                        .uri(COUPON_LB))
                .route(COUPON, p -> p.path("/api/coupon/policy/**")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(COUPON_LB))
                .route(COUPON, p -> p.path("/api/coupon/policy/register")
                        .uri(COUPON_LB))
                .route(COUPON, p -> p.path("/api/coupon/type")
                        .and().method("GET")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(COUPON_LB))
                .route("couponClient", p -> p.path("/api/client/coupon-payment")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(CLIENT_LB))
                .route(COUPON, p -> p.path("/api/coupon/adminPage")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(COUPON_LB))
                .route(POINT_ACCUMULATION, p -> p.path("/api/point/myPage/reward")
                        .and().method("GET")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route(POINT_ACCUMULATION, p -> p.path("/api/point/adminPage/reward")
                        .and().method("GET")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route(POINT_POLICY, p -> p.path("/api/point/policy/register")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route(POINT_POLICY, p -> p.path("/api/point/policy")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route(POINT_POLICY, p -> p.path("/api/point/policy/{pointPolicyId}")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route(POINT_POLICY, p -> p.path("/api/point/policy/modify")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route(POINT_POLICY, p -> p.path("/api/point/policy/active")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route("pointUsage", p -> p.path("/api/point/myPage/use")
                        .and().method("GET")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route("pointUsage", p -> p.path("/api/point/adminPage/use")
                        .and().method("GET")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route(POINT_ORDER, p -> p.path("/api/point/use/payment")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route(POINT_ORDER, p -> p.path("/api/point")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))
                .route(POINT_ORDER, p -> p.path("/api/point/order")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(POINT_LB))

                .route("search", p -> p.path("/api/search")
                        .and().method("GET")
                        .uri("lb://SEARCH"))

                .route(CLIENT, p -> p.path("/api/client/role", "/api/client/privacy-page")
                        .and().method("GET")
                        .filters(f -> f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(CLIENT_LB))
                .route(CLIENT, p -> p.path("/api/client/grade")
                        .and().method("PUT", "GET")
                        .uri(CLIENT_LB))
                .route("userName", p -> p.path("/api/client/name")
                        .and().method("GET")
                        .uri(CLIENT_LB))
                .route(POINT_ACCUMULATION, p -> p.path("/api/point/adminPage/delete/{pointAccumulationHistoryId}")
                        .and().method("DELETE")
                        .uri(POINT_LB))
                .route("birthdayUser", p -> p.path("/api/client/birth-coupon")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(CLIENT_LB))
                .route("paymentReward", p -> p.path("/api/coupon/payment/reward")
                        .filters(f -> f.filter(
                                jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(COUPON_LB))

                .route("refundOrderClient", p -> p.path("/api/refund/**")
                        .uri(ORDER_LB))
                .build();
    }
}
