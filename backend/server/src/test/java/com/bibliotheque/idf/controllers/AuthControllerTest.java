package com.bibliotheque.idf.controllers;

import devOps.controllers.AuthController;
import devOps.dtos.AuthDTO;
import devOps.dtos.JwtResponseDTO;
import devOps.dtos.LoginDTO;
import devOps.models.UtilisateurEntity;
import devOps.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthController authController;
    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        authController = new AuthController();
        authService = mock(AuthService.class);

        Field field = AuthController.class.getDeclaredField("authService");
        field.setAccessible(true);
        field.set(authController, authService);
    }

    @Test
    void register_shouldReturnCreatedWhenUserIsCreated() {
        AuthDTO authDTO = new AuthDTO();

        UtilisateurEntity user = new UtilisateurEntity();
        user.setUsername("assia");

        when(authService.register(authDTO)).thenReturn(user);

        var response = authController.register(authDTO);

        assertEquals(201, response.getStatusCode().value());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Utilisateur créé avec succès", body.get("message"));
        assertEquals("assia", body.get("username"));

        verify(authService).register(authDTO);
    }

    @Test
    void register_shouldReturnBadRequestWhenUserAlreadyExists() {
        AuthDTO authDTO = new AuthDTO();

        when(authService.register(authDTO))
                .thenThrow(new RuntimeException("Username déjà utilisé"));

        var response = authController.register(authDTO);

        assertEquals(400, response.getStatusCode().value());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Username déjà utilisé", body.get("error"));

        verify(authService).register(authDTO);
    }

    @Test
    void login_shouldReturnOkWhenCredentialsAreValid() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("assia");
        loginDTO.setPassword("password");

        JwtResponseDTO jwtResponse = new JwtResponseDTO();
        jwtResponse.setToken("fake-jwt-token");

        when(authService.login(loginDTO)).thenReturn(jwtResponse);

        var response = authController.login(loginDTO);

        assertEquals(200, response.getStatusCode().value());
        assertSame(jwtResponse, response.getBody());

        verify(authService).login(loginDTO);
    }

    @Test
    void login_shouldReturnUnauthorizedWhenCredentialsAreInvalid() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("assia");
        loginDTO.setPassword("wrong-password");

        when(authService.login(loginDTO))
                .thenThrow(new RuntimeException("Bad credentials"));

        var response = authController.login(loginDTO);

        assertEquals(401, response.getStatusCode().value());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Identifiants invalides", body.get("error"));

        verify(authService).login(loginDTO);
    }

    @Test
    void login_shouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("assia");
        loginDTO.setPassword("password");

        when(authService.login(loginDTO))
                .thenThrow(new Error("Erreur inattendue"));

        assertThrows(
                Error.class,
                () -> authController.login(loginDTO)
        );
    }
}