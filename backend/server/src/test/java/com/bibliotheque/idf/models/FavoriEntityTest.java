package devOps.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FavoriEntityTest {

    @Test
    void onCreate_shouldSetDateAjout() {
        FavoriEntity favori = new FavoriEntity();

        favori.onCreate(); // simule @PrePersist

        assertNotNull(favori.getDateAjout());
        assertTrue(favori.getDateAjout().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}