package com.nhnacademy.gateway2.filter;

import com.nhnacademy.gateway2.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.data.redis.core.RedisTemplate;
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

    private Mono<Void> handleExpired(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> handleInvalidToken(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("jwt-validation-filter");
            ServerHttpRequest request = exchange.getRequest();
            if (!request.getHeaders().containsKey("access")) {
                log.info("jwt-validation-filter access header missing");
                return handleInvalidToken(exchange);
            } else {
                String accessToken = request.getHeaders().getFirst("access");

                if (jwtUtils.isExpired(accessToken)) {
                    log.info("jwt-validation-filter expired");
                    return handleExpired(exchange);
                } else if (!jwtUtils.getCategory(accessToken).equals("access")) {
                    log.info("jwt-validation-filter notfound access");
                    return handleInvalidToken(exchange);
                }

                exchange.mutate().request(builder -> {
                    builder.header("X-User-Id", String.valueOf(redisTemplate.opsForHash().get(accessToken, jwtUtils.getUUID(accessToken))));
                    for (String role : jwtUtils.getRole(accessToken)) {
                        builder.header("X-User-Role", role);
                    }
                });

                log.info("X-User-Id :{}", request.getHeaders().getFirst("X-User-Id"));
            }

            return chain.filter(exchange);
        };
    }
}
