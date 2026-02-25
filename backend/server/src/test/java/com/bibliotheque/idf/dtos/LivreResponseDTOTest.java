package devOps.dtos;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LivreResponseDTO_Test {

    @Test
    void shouldSetAndGetAllFields() {
        LivreResponseDTO dto = new LivreResponseDTO();
        dto.setTitre("Le Petit Prince");
        dto.setAuteur("Antoine de Saint-Exupéry");
        dto.setBibliotheque("Biblio Nord");
        dto.setDisponible(true);
        dto.setCote("A12-345");
        dto.setIsbn("978-2070612758");

        assertThat(dto.getTitre()).isEqualTo("Le Petit Prince");
        assertThat(dto.isDisponible()).isTrue();
        assertThat(dto.getIsbn()).startsWith("978");
    }
}