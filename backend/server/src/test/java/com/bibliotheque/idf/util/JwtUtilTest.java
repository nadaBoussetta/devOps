package com.bibliotheque.idf.util;

import devOps.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET = "bibliothequeIdfSecretKeyForJWTTokenGenerationAndValidation2024";
    private static final long EXPIRATION = 86400000L; // 24h

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        UserDetails userDetails = User.withUsername("john.doe")
                .password("pass")
                .authorities("ROLE_USER")
                .build();

        String token = jwtUtil.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature

        String extractedUsername = jwtUtil.extractUsername(token);
        assertThat(extractedUsername).isEqualTo("john.doe");

        Date expiration = jwtUtil.extractExpiration(token);
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void validateToken_shouldReturnTrueForValidNonExpiredToken() {
        UserDetails userDetails = User.withUsername("alice")
                .password("pass")
                .authorities("ROLE_USER")
                .build();

        String token = jwtUtil.generateToken(userDetails);

        boolean valid = jwtUtil.validateToken(token, userDetails);

        assertTrue(valid);
    }

    @Test
    void validateToken_shouldReturnFalseWhenUsernameDoesNotMatch() {
        UserDetails userDetails = User.withUsername("bob")
                .password("pass")
                .authorities("ROLE_USER")
                .build();

        String token = jwtUtil.generateToken(User.withUsername("alice").password("pass").build());

        boolean valid = jwtUtil.validateToken(token, userDetails);

        assertFalse(valid);
    }


    @Test
    void extractClaim_shouldWorkWithCustomResolver() {
        UserDetails userDetails = User.withUsername("extract.test").password("pass").build();
        String token = jwtUtil.generateToken(userDetails);

        Date issuedAt = jwtUtil.extractClaim(token, Claims::getIssuedAt);

        assertThat(issuedAt).isBeforeOrEqualTo(new Date());
    }
}