package com.bibliotheque.idf.dtos;

import devOps.dtos.JwtResponseDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class JwtResponseDTOTest {

    @Test
    void shouldCreateWithAllArgsConstructorAndDefaultType() {
        JwtResponseDTO dto = new JwtResponseDTO("jwt.token.here", 42L, "john.doe", "john@example.com");

        assertThat(dto.getToken()).isEqualTo("jwt.token.here");
        assertThat(dto.getUserId()).isEqualTo(42L);
        assertThat(dto.getUsername()).isEqualTo("john.doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
        assertThat(dto.getType()).isEqualTo("Bearer");
    }

    @Test
    void shouldAllowSettingTypeManually() {
        JwtResponseDTO dto = new JwtResponseDTO();
        dto.setToken("custom.token");
        dto.setType("CustomBearer");
        dto.setUserId(100L);
        dto.setUsername("alice");
        dto.setEmail("alice@company.com");

        assertThat(dto.getType()).isEqualTo("CustomBearer");
    }

    @Test
    void noArgsConstructorShouldInitializeWithDefaultType() {
        JwtResponseDTO dto = new JwtResponseDTO();
        assertThat(dto.getType()).isEqualTo("Bearer");
        assertThat(dto.getToken()).isNull();
        assertThat(dto.getUserId()).isNull();
    }

    @Test
    void shouldHaveProperEqualsAndHashCode() {
        JwtResponseDTO dto1 = new JwtResponseDTO("token123", 1L, "user1", "u1@mail.com");
        JwtResponseDTO dto2 = new JwtResponseDTO("token123", 1L, "user1", "u1@mail.com");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

}