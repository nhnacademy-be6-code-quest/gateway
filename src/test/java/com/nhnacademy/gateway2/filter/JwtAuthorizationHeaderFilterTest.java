package com.nhnacademy.gateway2.filter;

import com.nhnacademy.gateway2.utils.JWTUtils;
import com.nhnacademy.gateway2.utils.TransformerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class JwtAuthorizationHeaderFilterTest {

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private TransformerUtils transformerUtils;

    private JwtAuthorizationHeaderFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthorizationHeaderFilter(jwtUtils, transformerUtils);
    }

    @Test
    void testFilterWithNoAccessHeader() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        GatewayFilter gatewayFilter = filter.apply(new JwtAuthorizationHeaderFilter.Config());

        StepVerifier.create(gatewayFilter.filter(exchange, serverWebExchange -> Mono.empty()))
                .verifyComplete();

        verifyNoInteractions(jwtUtils);
    }

    @Test
    void testFilterWithExpiredToken() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").header("access", "expired_token").build());
        when(jwtUtils.isExpired("expired_token")).thenReturn(true);

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthorizationHeaderFilter.Config());

        StepVerifier.create(gatewayFilter.filter(exchange, serverWebExchange -> Mono.empty()))
                .verifyComplete();

        verify(jwtUtils).isExpired("expired_token");
        assert exchange.getResponse().getStatusCode().value() == 303;
    }

    @Test
    void testFilterWithInvalidTokenCategory() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").header("access", "invalid_token").build());
        when(jwtUtils.isExpired("invalid_token")).thenReturn(false);
        when(jwtUtils.getCategory("invalid_token")).thenReturn("refresh");

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthorizationHeaderFilter.Config());

        StepVerifier.create(gatewayFilter.filter(exchange, serverWebExchange -> Mono.empty()))
                .verifyComplete();

        verify(jwtUtils).isExpired("invalid_token");
        verify(jwtUtils).getCategory("invalid_token");
        assert exchange.getResponse().getStatusCode().value() == 401;
    }

    @Test
    void testFilterWithValidToken() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").header("access", "valid_token").build());
        when(jwtUtils.isExpired("valid_token")).thenReturn(false);
        when(jwtUtils.getCategory("valid_token")).thenReturn("access");
        when(jwtUtils.getUUID("valid_token")).thenReturn("encoded_uuid");
        when(jwtUtils.getRole("valid_token")).thenReturn(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
        when(transformerUtils.decode("encoded_uuid")).thenReturn("decoded_uuid");

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthorizationHeaderFilter.Config());

        StepVerifier.create(gatewayFilter.filter(exchange, serverWebExchange -> Mono.empty()))
                .verifyComplete();

        verify(jwtUtils).isExpired("valid_token");
        verify(jwtUtils).getCategory("valid_token");
        verify(jwtUtils).getUUID("valid_token");
        verify(jwtUtils).getRole("valid_token");
        verify(transformerUtils).decode("encoded_uuid");

        assert exchange.getRequest().getHeaders().getFirst("X-User-Id").equals("decoded_uuid");
    }
}