package com.bibliotheque.idf.controllers;

import devOps.controllers.NotificationController;
import devOps.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    private NotificationController notificationController;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() throws Exception {
        notificationController = new NotificationController();
        notificationService = mock(NotificationService.class);

        Field field = NotificationController.class.getDeclaredField("notificationService");
        field.setAccessible(true);
        field.set(notificationController, notificationService);
    }

    @Test
    void getNotifications_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> notificationController.getNotifications(null));
    }

    @Test
    void getNotificationsNonLues_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> notificationController.getNotificationsNonLues(null));
    }

    @Test
    void countNotificationsNonLues_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> notificationController.countNotificationsNonLues(null));
    }

    @Test
    void marquerCommeLue_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> notificationController.marquerCommeLue(1L, null));
    }

    @Test
    void marquerToutesCommeLues_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> notificationController.marquerToutesCommeLues(null));
    }

    @Test
    void supprimerNotification_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> notificationController.supprimerNotification(1L, null));
    }

    @Test
    void supprimerNotificationsLues_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> notificationController.supprimerNotificationsLues(null));
    }
}