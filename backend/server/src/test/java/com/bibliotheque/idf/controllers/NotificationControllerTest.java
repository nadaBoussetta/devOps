package com.bibliotheque.idf.controllers;  // adapte si ton package est devOps.controllers

import com.bibliotheque.idf.services.NotificationService;
import com.bibliotheque.idf.util.SecurityUtil; // si tu utilises SecurityUtil pour extraire l'userId
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du NotificationController")
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        // Reset mocks si besoin entre les tests
        reset(notificationService);
    }

    @Test
    @DisplayName("GET /notifications/count-non-lues → 3 notifications non lues → retourne 200 + 3")
    void countNotificationsNonLues_shouldReturnThree() {
        // Arrange
        Long userId = 1L;
        when(notificationService.countNotificationsNonLues(userId)).thenReturn(3);

        // Mock SecurityUtil si la méthode est privée et utilise SecurityUtil.getCurrentUserId()
        try (MockedStatic<SecurityUtil> mockedSecurity = mockStatic(SecurityUtil.class)) {
            mockedSecurity.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            // Act
            ResponseEntity<Integer> response = notificationController.countNotificationsNonLues();

            // Assert
            assertThat(response.getStatusCodeValue()).isEqualTo(200);
            assertThat(response.getBody()).isNotNull().isEqualTo(3);
        }
    }

    @Test
    @DisplayName("GET /notifications/count-non-lues → 0 notification non lue → retourne 200 + 0")
    void countNotificationsNonLues_zeroNotifications_shouldReturnZero() {
        // Arrange
        Long userId = 1L;
        when(notificationService.countNotificationsNonLues(userId)).thenReturn(0);

        try (MockedStatic<SecurityUtil> mockedSecurity = mockStatic(SecurityUtil.class)) {
            mockedSecurity.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            // Act
            ResponseEntity<Integer> response = notificationController.countNotificationsNonLues();

            // Assert
            assertThat(response.getStatusCodeValue()).isEqualTo(200);
            assertThat(response.getBody()).isNotNull().isEqualTo(0);
        }
    }

    @Test
    @DisplayName("GET /notifications/count-non-lues → utilisateur inconnu ou non connecté → retourne 0 ou 401")
    void countNotificationsNonLues_userNotFound_shouldReturnZeroOrUnauthorized() {
        // Variante 1 : service retourne 0 quand userId est invalide
        when(notificationService.countNotificationsNonLues(anyLong())).thenReturn(0);

        try (MockedStatic<SecurityUtil> mockedSecurity = mockStatic(SecurityUtil.class)) {
            mockedSecurity.when(SecurityUtil::getCurrentUserId).thenReturn(null); // ou lance une exception

            ResponseEntity<Integer> response = notificationController.countNotificationsNonLues();

            assertThat(response.getStatusCodeValue()).isIn(200, 401);
            assertThat(response.getBody()).isNotNull().isEqualTo(0);
        }

        // Variante 2 : si ton controller lance une exception → tu peux tester avec assertThrows
    }
}