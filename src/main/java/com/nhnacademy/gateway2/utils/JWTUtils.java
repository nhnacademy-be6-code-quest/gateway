package com.nhnacademy.gateway2.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTUtils {
    private final SecretKey jwtSecretKey;

    public String getCategory(String token) {
        return getClaimsFromToken(token).get("category", String.class);
    }

    public String getUUID(String token) {
        return getClaimsFromToken(token).get("uuid", String.class);
    }

    public List<String> getRole(String token) {
        return getClaimsFromToken(token).get("role", List.class);
    }

    public boolean isExpired(String token) {
        try {
            return getClaimsFromToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(jwtSecretKey).build().parseSignedClaims(token).getPayload();
    }
}
