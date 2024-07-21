package com.nhnacademy.gateway2.config;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class KeyManagerConfigTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KeyManagerConfig keyManagerConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(keyManagerConfig, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(keyManagerConfig, "accessKeyId", "test-access-key-id");
        ReflectionTestUtils.setField(keyManagerConfig, "accessKeySecret", "test-access-key-secret");
        ReflectionTestUtils.setField(keyManagerConfig, "jwtSecret", "jwt-secret-key");
        ReflectionTestUtils.setField(keyManagerConfig, "keyClientEncoding", "client-encoding-key");
    }

    private void mockRestTemplateResponse(String secretValue) {
        String jsonResponse = "{\"body\":{\"secret\":\"" + secretValue + "\"}}";
        ResponseEntity<String> responseEntity = ResponseEntity.ok(jsonResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
    }

    @Test
    void testJwtSecretKey() {
        mockRestTemplateResponse("test-jwt-secret-key");
        SecretKey secretKey = keyManagerConfig.jwtSecretKey();
        assertNotNull(secretKey);
        assertEquals(Jwts.SIG.HS256.key().build().getAlgorithm(), secretKey.getAlgorithm());
    }

    @Test
    void testClientEncodingKey() {
        mockRestTemplateResponse("test-client-encoding-key");
        String clientEncodingKey = keyManagerConfig.clientEncodingKey();
        assertEquals("test-client-encoding-key", clientEncodingKey);
    }
}