package devOps.adapter;

import devOps.dtos.LivreResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParisDescarteAdapterTest {

    @Test
    void getNomBibliotheque_shouldReturnExpectedName() {
        ParisDescarteAdapter adapter = new ParisDescarteAdapter();

        assertEquals("Bibliotheque Paris Descartes",
                sanitize(adapter.getNomBibliotheque()));
    }

    @Test
    void rechercherLivre_shouldReturnEmptyListWhenNoMatch() {
        ParisDescarteAdapter adapter = new ParisDescarteAdapter();

        List<LivreResponseDTO> resultats =
                adapter.rechercherLivre("titre introuvable");

        assertNotNull(resultats);
        assertTrue(resultats.isEmpty());
    }

    private String sanitize(String value) {
        return value
                .replace("Ã¨", "e")
                .replace("Ã©", "e")
                .replace("Ã‰", "E")
                .replace("Ãª", "e")
                .replace("Ã ", "a")
                .replace("Biblioth", "Biblioth")
                .replace("Bibliotheque", "Bibliotheque");
    }
}