package devOps.services;

import devOps.dtos.AuthDTO;
import devOps.dtos.JwtResponseDTO;
import devOps.dtos.LoginDTO;
import devOps.models.UtilisateurEntity;
import devOps.repositories.UtilisateurRepository;
import devOps.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UtilisateurRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldSaveEncodedUser() {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("alice");
        authDTO.setEmail("alice@mail.com");
        authDTO.setPassword("secret");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRepository.save(any(UtilisateurEntity.class))).thenAnswer(invocation -> {
            UtilisateurEntity u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UtilisateurEntity saved = authService.register(authDTO);

        assertEquals(1L, saved.getId());
        assertEquals("encoded", saved.getPassword());
        assertTrue(saved.getRoles().contains("ROLE_USER"));
        verify(userRepository).save(any(UtilisateurEntity.class));
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("alice");

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(authDTO));
    }

    @Test
    void login_shouldReturnJwtResponse() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("alice");
        loginDTO.setPassword("secret");

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = User.withUsername("alice").password("encoded").authorities("ROLE_USER").build();

        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(10L);
        user.setUsername("alice");
        user.setEmail("alice@mail.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        JwtResponseDTO response = authService.login(loginDTO);

        assertEquals("jwt-token", response.getToken());
        assertEquals(10L, response.getUserId());
        assertEquals("alice", response.getUsername());
    }
}