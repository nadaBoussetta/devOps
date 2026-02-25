package devOps.models;

import devOps.enums.TypeNotification;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationEntityTest {

    @Test
    void onCreate_shouldSetDateCreation() {
        NotificationEntity notification = new NotificationEntity();

        notification.onCreate(); // simule @PrePersist

        assertNotNull(notification.getDateCreation());
        assertTrue(notification.getDateCreation().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void marquerCommeLue_shouldSetLueAndDateConsultation() {
        NotificationEntity notification = new NotificationEntity();
        notification.setLue(false);

        notification.marquerCommeLue();

        assertTrue(notification.getLue());
        assertNotNull(notification.getDateConsultation());
    }
}