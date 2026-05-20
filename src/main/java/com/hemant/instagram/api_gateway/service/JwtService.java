package com.hemant.instagram.api_gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@NoArgsConstructor
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignature())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private SecretKey getSignature() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
