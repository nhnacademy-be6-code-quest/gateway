package com.nhnacademy.gateway2.filter;

import com.nhnacademy.gateway2.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendUserIdOfHeaderFilter extends AbstractGatewayFilterFactory<SendUserIdOfHeaderFilter.Config> {
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("send-userId-filter");
            ServerHttpRequest request = exchange.getRequest();
            if (!request.getHeaders().containsKey("access")) {
                log.info("it is guest");
            } else {
                String accessToken = request.getHeaders().getFirst("access");

                String userId;
                if (jwtUtils.isExpired(accessToken)) {
                    userId = null;
                    log.info("jwt-validation-filter expired");
                } else if (!jwtUtils.getCategory(accessToken).equals("access")) {
                    userId = null;
                    log.info("jwt-validation-filter notfound access");
                } else{
                    userId = String.valueOf(redisTemplate.opsForHash().get(accessToken, jwtUtils.getUUID(accessToken)));
                }

                exchange.mutate().request(builder -> {
                    builder.header("X-User-Id", userId);
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
