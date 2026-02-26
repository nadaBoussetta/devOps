package com.bibliotheque.idf.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class JwtAuthenticationFilterTest {

    @Test
    void classShouldLoad() {
        assertDoesNotThrow(() -> Class.forName("devOps.config.JwtAuthenticationFilter"));
    }
}