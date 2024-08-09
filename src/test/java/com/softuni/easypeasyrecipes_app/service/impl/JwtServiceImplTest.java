package com.softuni.easypeasyrecipes_app.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtServiceImplTest {
    private static final String SECRET_KEY = "Irena-256-bit-secret-key-irena-256-bit-secret-key";
    private static final long EXPIRATION_TIME = 3600000;

    private JwtServiceImpl jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtServiceImpl(SECRET_KEY, EXPIRATION_TIME);
    }

    @Test
    public void testGenerateToken() {
        String userId = "12345";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtService.generateToken(userId, claims);

        assertNotNull(token);

        Key signingKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        Claims parsedClaims = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();

        assertEquals(userId, parsedClaims.getSubject());
        assertEquals("USER", parsedClaims.get("role"));
    }
}
