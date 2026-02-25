package devOps.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void passwordEncoder_shouldBeBCrypt() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void authenticationProvider_shouldBeConfiguredCorrectly() {
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();

        assertThat(provider.getUserDetailsService()).isSameAs(userDetailsService);
        assertThat(provider.getPasswordEncoder()).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void authenticationManager_shouldBeCreated() throws Exception {
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = securityConfig.authenticationManager(config);
        assertThat(manager).isNotNull();
    }

    @Test
    void securityFilterChain_shouldBeCreated() throws Exception {
        SecurityFilterChain chain = securityConfig.securityFilterChain(mockHttpSecurity());
        assertThat(chain).isNotNull();
    }

    // petit helper pour éviter l'exception réelle
    private HttpSecurity mockHttpSecurity() {
        // Mock complexe → on retourne juste un mock
        return mock(HttpSecurity.class);
    }
}