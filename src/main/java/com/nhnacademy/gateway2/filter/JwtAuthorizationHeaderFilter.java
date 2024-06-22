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
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
        return exchange.getResponse().setComplete();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return  (exchange, chain)->{
            log.info("jwt-validation-filter");
            ServerHttpRequest request = exchange.getRequest();
            if(!request.getHeaders().containsKey("access")) {
                log.info("jwt-validation-filter access header missing");
                return handleUnauthorized(exchange);
            }else{
                String accessToken = request.getHeaders().getFirst("access");
                String refreshToken = request.getHeaders().getFirst("refresh");

                if (jwtUtils.isExpired(accessToken)) {
                    log.info("jwt-validation-filter expired");
                    return handleUnauthorized(exchange);
                } else if (!jwtUtils.getCategory(accessToken).equals("access")) {
                    log.info("jwt-validation-filter notfound access");
                    return handleUnauthorized(exchange);
                } else if (redisTemplate.opsForHash().get(refreshToken, jwtUtils.getUUID(refreshToken)) == null) {
                    log.info("jwt-validation-filter refresh unauthorized");
                    return handleUnauthorized(exchange);
                }

                log.debug("accessToken:{}",accessToken);

                exchange.mutate().request(builder -> {
                    builder.header("email",(String) redisTemplate.opsForHash().get(refreshToken, jwtUtils.getUUID(accessToken)));
                    builder.header("role", jwtUtils.getRole(accessToken));
                });
            }

            return chain.filter(exchange);
        };
    }
}
