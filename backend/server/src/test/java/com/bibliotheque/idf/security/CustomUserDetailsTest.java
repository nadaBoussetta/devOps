package com.bibliotheque.idf.security;

import devOps.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    private CustomUserDetails userDetails;
    private final String username = "assia_dev";
    private final String password = "$2a$10$hashedpasswordexample";
    private final Long userId = 42L;
    private final Collection<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
    );

    @BeforeEach
    void setUp() {
        userDetails = new CustomUserDetails(username, password, authorities, userId);
    }

    @Test
    void constructor_shouldInitializeAllFieldsCorrectly() {
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities()).containsExactlyInAnyOrderElementsOf(authorities);
        assertThat(userDetails.getUserId()).isEqualTo(userId);
    }

    @Test
    void getUserId_shouldReturnTheProvidedId() {
        assertThat(userDetails.getUserId()).isEqualTo(42L);
    }

    @Test
    void defaultAccountStatus_shouldBeAllTrue() {
        // Par défaut, User considère les comptes valides si non explicitement désactivés
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void toString_shouldIncludeUsernameAndAuthorities() {
        String str = userDetails.toString();

        assertThat(str).contains(username);
        assertThat(str).contains("ROLE_USER");
        assertThat(str).contains("ROLE_ADMIN");

        assertThat(str).doesNotContain("password");
    }

    @Test
    void canBeUsedAsRegularUserInSecurityContext() {
        User casted = userDetails;
        assertThat(casted.getUsername()).isEqualTo(username);
        assertThat(casted.getAuthorities()).hasSize(2);
    }
}