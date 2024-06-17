package com.nhnacademy.gateway2.Config;

import com.nhnacademy.gateway2.filter.JwtAuthorizationHeaderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RouteLocatorConfig {
    private final JwtAuthorizationHeaderFilter jwtAuthorizationHeaderFilter;

    @Bean
    public RouteLocator myRoute(RouteLocatorBuilder builder ) {
        //TODO#1 router설정, gateway는 모든 요청의 진입점 입니다.

        //TODO#1-3 서비스 명과 path는 추후 수정 됩니다.
        return builder.routes()
                .route("auth-service", p->p.path("/login")
                        //TODO#1-3 jwt를 검증할 Filter를 등록합니다
                        .uri("lb://AUTH-SERVICE")
                )
                //TODO#1-2 order 서비스는 jwtAuthorizationHeaderFilter가 적용됩니다. (인증 없이 접근 시 로그인 페이지로 이동)
                .route("order-service", p->p.path("/order")
                        .filters(f->f.filter(jwtAuthorizationHeaderFilter.apply(new JwtAuthorizationHeaderFilter.Config())))
                        .uri(("lb://ORDER-SERVICE")
                        ))
                .build();

    }
}
