package com.nhnacademy.gateway2.config;

import io.jsonwebtoken.Jwts;
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
public class KeyManagerConfig {
    private static final String URL = "https://api-keymanager.nhncloudservice.com/keymanager/v1.2/appkey/";
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

    @Bean
    public SecretKey jwtSecretKey() {
        String jwtSecretKey = getKey(new RestTemplate().exchange(
                getURL(jwtSecret),
                HttpMethod.GET,
                getAccessHeaders(),
                JSONObject.class
        ).getBody());
        log.info("JWT Secret Key: {}", jwtSecretKey);
        return new SecretKeySpec(jwtSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    @Bean
    public String clientEncodingKey() {
        String clientEncoding = getKey(new RestTemplate().exchange(
                getURL(keyClientEncoding),
                HttpMethod.GET,
                getAccessHeaders(),
                JSONObject.class
        ).getBody());
        log.info("Client Encoding Key: {}", clientEncoding);
        return clientEncoding;
    }

    private HttpEntity<String> getAccessHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-TC-AUTHENTICATION-ID", accessKeyId);
        headers.add("X-TC-AUTHENTICATION-SECRET", accessKeySecret);
        return new HttpEntity<>(headers);
    }

    private String getKey(JSONObject jsonObject) {
        Map<String, Object> responseMap = jsonObject;
        Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
        return (String) bodyMap.get("secret");
    }

    private String getURL(String secretKey) {
        return URL + apiKey + "/secrets/" + secretKey;
    }
}
