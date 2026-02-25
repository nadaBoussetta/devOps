package devOps.dtos;

import devOps.enums.TypeNotification;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDTOTest {

    @Test
    void shouldSetAndGetValuesCorrectly() {
        NotificationDTO dto = new NotificationDTO(
                1L,
                TypeNotification.INFO,
                "Titre",
                "Message",
                2L,
                "BU",
                false,
                LocalDateTime.now(),
                null
        );

        assertEquals("Titre", dto.getTitre());
        assertFalse(dto.getLue());
    }
}