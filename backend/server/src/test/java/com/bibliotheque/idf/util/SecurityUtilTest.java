package devOps.util;

import devOps.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        // On nettoie le context avant chaque test pour éviter les interférences
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_shouldReturnUserIdWhenCustomUserDetailsPresent() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(42L);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        Long userId = SecurityUtil.getCurrentUserId();

        assertThat(userId).isEqualTo(42L);
        verify(authentication).isAuthenticated();
        verify(authentication).getPrincipal();
    }

    @Test
    void getCurrentUserId_shouldReturnNullWhenNotAuthenticated() {
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);

        assertThat(SecurityUtil.getCurrentUserId()).isNull();
    }

    @Test
    void getCurrentUserId_shouldReturnNullWhenPrincipalNotCustomUserDetails() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("just-a-string");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        assertThat(SecurityUtil.getCurrentUserId()).isNull();
    }

    @Test
    void getCurrentUsername_shouldReturnNameWhenAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("assia_dev");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        assertThat(SecurityUtil.getCurrentUsername()).isEqualTo("assia_dev");
    }

    @Test
    void getCurrentUsername_shouldReturnNullWhenNotAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThat(SecurityUtil.getCurrentUsername()).isNull();
    }

    @Test
    void isAuthenticated_shouldReturnTrueWhenValidAuthentication() {
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(SecurityUtil.isAuthenticated()).isTrue();
    }

    @Test
    void isAuthenticated_shouldReturnFalseWhenNoAuthentication() {
        SecurityContextHolder.clearContext();
        assertThat(SecurityUtil.isAuthenticated()).isFalse();
    }
}