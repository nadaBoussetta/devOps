package devOps.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SessionEntityTest {

    @Test
    void onCreate_shouldSetDateCreationAndDateDebut() {
        SessionEntity session = new SessionEntity();
        session.setDureeMinutes(60);
        session.setTempsEcoulesMinutes(0);

        session.onCreate(); // simule @PrePersist

        assertNotNull(session.getDateCreation());
        assertNotNull(session.getDateDebut());
        assertTrue(session.getDateCreation().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void completer_shouldSetCompleteeAndDateFin() {
        SessionEntity session = new SessionEntity();
        session.setCompletee(false);

        session.completer();

        assertTrue(session.getCompletee());
        assertNotNull(session.getDateFin());
    }

    @Test
    void getTempsRestantMinutes_shouldReturnDifference() {
        SessionEntity session = new SessionEntity();
        session.setDureeMinutes(60);
        session.setTempsEcoulesMinutes(15);

        assertEquals(45, session.getTempsRestantMinutes());
    }

    @Test
    void getProgressionPourcentage_shouldReturnZeroWhenDureeZero() {
        SessionEntity session = new SessionEntity();
        session.setDureeMinutes(0);
        session.setTempsEcoulesMinutes(10);

        assertEquals(0.0, session.getProgressionPourcentage());
    }

    @Test
    void getProgressionPourcentage_shouldReturnCorrectValue() {
        SessionEntity session = new SessionEntity();
        session.setDureeMinutes(100);
        session.setTempsEcoulesMinutes(25);

        assertEquals(25.0, session.getProgressionPourcentage());
    }
}