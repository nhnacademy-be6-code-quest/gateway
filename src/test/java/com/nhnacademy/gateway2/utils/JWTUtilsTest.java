package com.nhnacademy.gateway2.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilsTest {

    private JWTUtils jwtUtils;
    private final String secretKeyString = "yourSecretKeyHereThatIsAtLeast32BytesLong";
    private final SecretKey secretKey = new SecretKeySpec(secretKeyString.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

    @BeforeEach
    void setUp() {
        jwtUtils = new JWTUtils(secretKey);
    }

    private String createTestToken(String category, String uuid, List<String> roles, long expirationMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("uuid", uuid)
                .claim("role", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    @Test
    void testGetCategory() {
        String token = createTestToken("access", "test-uuid", Arrays.asList("ROLE_USER"), 3600000L);
        assertEquals("access", jwtUtils.getCategory(token));
    }

    @Test
    void testGetUUID() {
        String token = createTestToken("access", "test-uuid", Arrays.asList("ROLE_USER"), 3600000L);
        assertEquals("test-uuid", jwtUtils.getUUID(token));
    }

    @Test
    void testGetRole() {
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        String token = createTestToken("access", "test-uuid", roles, 3600000L);
        assertEquals(roles, jwtUtils.getRole(token));
    }

    @Test
    void testIsExpired() {
        String validToken = createTestToken("access", "test-uuid", Arrays.asList("ROLE_USER"), 3600000L);
        assertFalse(jwtUtils.isExpired(validToken));

        String expiredToken = createTestToken("access", "test-uuid", Arrays.asList("ROLE_USER"), -1000L);
        assertTrue(jwtUtils.isExpired(expiredToken));
    }

    @Test
    void testGetClaimsFromToken() {
        String token = createTestToken("access", "test-uuid", Arrays.asList("ROLE_USER"), 3600000L);
        Claims claims = ReflectionTestUtils.invokeMethod(jwtUtils, "getClaimsFromToken", token);
        assertNotNull(claims);
        assertEquals("access", claims.get("category"));
        assertEquals("test-uuid", claims.get("uuid"));
        assertEquals(Arrays.asList("ROLE_USER"), claims.get("role"));
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalidToken";
        assertThrows(Exception.class, () -> jwtUtils.getCategory(invalidToken));
        assertThrows(Exception.class, () -> jwtUtils.getUUID(invalidToken));
        assertThrows(Exception.class, () -> jwtUtils.getRole(invalidToken));
        assertTrue(jwtUtils.isExpired(invalidToken));
    }
}