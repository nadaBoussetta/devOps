package devOps.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NotationEntityTest {

    @Test
    void onCreate_shouldSetDateNotationToToday() {
        NotationEntity notation = new NotationEntity();

        notation.onCreate(); // simule @PrePersist

        assertNotNull(notation.getDateNotation());
        assertEquals(LocalDate.now(), notation.getDateNotation());
    }
}