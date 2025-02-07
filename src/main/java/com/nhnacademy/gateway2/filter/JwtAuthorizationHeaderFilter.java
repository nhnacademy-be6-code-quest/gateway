package com.nhnacademy.gateway2.filter;

import com.nhnacademy.gateway2.utils.JWTUtils;
import com.nhnacademy.gateway2.utils.TransformerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
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
    private static final String ACCESS = "access";

    private final JWTUtils jwtUtils;
    private final TransformerUtils transformerUtils;

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
            if (!request.getHeaders().containsKey(ACCESS)) {
                log.info("jwt-validation-filter access header missing");
                return chain.filter(exchange);
            } else {
                String accessToken = request.getHeaders().getFirst(ACCESS);

                if (jwtUtils.isExpired(accessToken)) {
                    log.info("jwt-validation-filter expired");
                    return handleExpired(exchange);
                } else if (!jwtUtils.getCategory(accessToken).equals(ACCESS)) {
                    log.info("jwt-validation-filter notfound access");
                    return handleInvalidToken(exchange);
                }

                exchange.mutate().request(builder -> {
                    builder.header("X-User-Id", transformerUtils.decode(jwtUtils.getUUID(accessToken)));
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
