package com.nhnacademy.gateway2.filter;


import java.net.URI;

import com.nhnacademy.gateway2.utils.JWTUtils;
import com.nhnacademy.gateway2.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationHeaderFilter extends AbstractGatewayFilterFactory<JwtAuthorizationHeaderFilter.Config> {
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    public static class Config {
        // application.properties 파일에서 지정한 filer의 Argument값을 받는 부분
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER); // 303 See Other
        //exchange.getResponse().getHeaders().setLocation(URI.create("https://book-store.shop/login")); // 로그인 페이지로 리다이렉트
        return exchange.getResponse().setComplete();
    }
    //해당 url로 이동시킴.

    @Override
    public GatewayFilter apply(Config config) {
        return  (exchange, chain)->{
            log.debug("jwt-validation-filter");
            ServerHttpRequest request = exchange.getRequest();

            if(!request.getHeaders().containsKey("access")) {
                //TODO#3-1 Header에 Authorization 존재하지 않는다면 적절한 예외처리를 합니다. 현재는 https://book-store.shop/login 로 이동시킵니다.
                return handleUnauthorized(exchange);
            }else{
                String accessToken = request.getHeaders().getFirst("access");
                String refreshToken = request.getHeaders().getFirst("refresh");
                //TODO#3-2 AccessToken jjwt 라이브러리를 사용하여 검증 구현하기

                if (jwtUtils.isExpired(accessToken)) { //이미 Token이 만료되었는지?
                    return handleUnauthorized(exchange);
                } else if (!jwtUtils.getCategory(accessToken).equals("access")) { //Token의 signature 값 검증(HMAC)
                    return handleUnauthorized(exchange);
                } else if (redisTemplate.opsForHash().get(refreshToken, RedisUtils.getTokenPrefix()) == null) { //이미 로그아웃된 Token 인지? - Black List 관리
                    return handleUnauthorized(exchange);
                }

                //account-api의 JwtProperties를 참고하여 구현합니다.

//                String accessToken = request.getHeaders().get(HttpHeaders.AUTHORIZATION).toString();
                log.debug("accessToken:{}",accessToken);

                //TODO#3-3 검증이 완료되면  Request header에 X-USER-ID를 등록합니다.
                //exchange.getRequest().getHeaders(); <-- imutable 합니다. 즉 수정 할 수 없습니다.
                //exchage.mutate()를 이용해야 합니다. 아래 코드를 참고하세요.

                exchange.mutate().request(builder -> {
                    builder.header("X-USER-ID","nhnacademy");
                });
            }

            return chain.filter(exchange);
        };
    }
}
