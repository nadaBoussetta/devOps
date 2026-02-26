package devOps.controllers;

import devOps.dtos.AuthDTO;
import devOps.dtos.JwtResponseDTO;
import devOps.dtos.LoginDTO;
import devOps.models.UtilisateurEntity;
import devOps.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires du AuthController")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("POST /register → inscription réussie → 201 Created + utilisateur retourné")
    void register_success_shouldReturn201AndUser() {
        // Arrange
        AuthDTO dto = new AuthDTO();
        dto.setUsername("testUser");
        dto.setPassword("password123");

        UtilisateurEntity createdUser = new UtilisateurEntity();
        createdUser.setId(42L);
        createdUser.setUsername("testUser");

        when(authService.register(any(AuthDTO.class))).thenReturn(createdUser);

        // Act
        ResponseEntity<?> response = authController.register(dto);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(UtilisateurEntity.class);
        assertThat(((UtilisateurEntity) response.getBody()).getUsername()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("POST /login → connexion réussie → 200 OK + JWT retourné")
    void login_success_shouldReturn200AndJwt() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testUser");
        loginDTO.setPassword("password123");

        JwtResponseDTO jwtResponse = new JwtResponseDTO("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        when(authService.login(any(LoginDTO.class))).thenReturn(jwtResponse);

        // Act
        ResponseEntity<?> response = authController.login(loginDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(JwtResponseDTO.class);
        assertThat(((JwtResponseDTO) response.getBody()).getToken()).isNotBlank();
    }

    @Test
    @DisplayName("POST /login → identifiants invalides → 401 Unauthorized")
    void login_invalidCredentials_shouldReturn401() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("wrong");
        loginDTO.setPassword("wrong");

        when(authService.login(any(LoginDTO.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));  // adapte selon ton exception réelle

        // Act
        ResponseEntity<?> response = authController.login(loginDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull(); // ou un message d'erreur selon ton impl
    }
}