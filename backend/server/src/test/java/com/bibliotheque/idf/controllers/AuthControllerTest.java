package devOps.controllers;

import devOps.dtos.*;
import devOps.models.UtilisateurEntity;
import devOps.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_success() {
        AuthDTO dto = new AuthDTO();
        UtilisateurEntity user = new UtilisateurEntity();
        user.setUsername("testUser");

        when(authService.register(dto)).thenReturn(user);

        ResponseEntity<?> response = authController.register(dto);

        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void login_success() {
        LoginDTO loginDTO = new LoginDTO();
        JwtResponseDTO jwt = new JwtResponseDTO("token");

        when(authService.login(loginDTO)).thenReturn(jwt);

        ResponseEntity<?> response = authController.login(loginDTO);

        assertEquals(200, response.getStatusCodeValue());
    }
}