package com.nhnacademy.gateway2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KeyManagerConfig {
    private static final String BASE_URL = "https://api-keymanager.nhncloudservice.com/keymanager/v1.2/appkey/";
    @Value("${key-manager.api.key}")
    private String apiKey;
    @Value("${user.access.key.id}")
    private String accessKeyId;
    @Value("${secret.access.key}")
    private String accessKeySecret;
    @Value("${secret.key.jwt}")
    private String jwtSecret;
    @Value("${secret.key.client.encoding}")
    private String keyClientEncoding;

    private final RestTemplate restTemplate;

    @Bean
    public SecretKey jwtSecretKey() {
        String jwtSecretKey = getKey(getSecret(jwtSecret));
        log.info("JWT Secret Key: {}", jwtSecretKey);
        return new SecretKeySpec(jwtSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    @Bean
    public String clientEncodingKey() {
        String clientEncoding = getKey(getSecret(keyClientEncoding));
        log.info("Client Encoding Key: {}", clientEncoding);
        return clientEncoding;
    }

    private String getKey(String jsonResponse) {
        try {
            Map<String, Object> responseMap = new ObjectMapper().readValue(jsonResponse, Map.class);
            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
            return (String) bodyMap.get("secret");
        } catch (Exception e) {
            log.error("Error parsing JSON response", e);
            return null;
        }
    }

    private String getSecret(String secretKey) {
        String url = BASE_URL + apiKey + "/secrets/" + secretKey;
        HttpHeaders headers = getAccessHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }

    private HttpHeaders getAccessHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-TC-AUTHENTICATION-ID", accessKeyId);
        headers.add("X-TC-AUTHENTICATION-SECRET", accessKeySecret);
        return headers;
    }
}
