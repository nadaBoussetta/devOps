package devOps.dtos;

import devOps.enums.TypeBibliotheque;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LibraryResponseDTO_Test {

    @Test
    void shouldCreateWithAllFields() {
        HoraireDTO h1 = new HoraireDTO(1L, "Lundi", "09:00", "18:00");
        LibraryResponseDTO dto = new LibraryResponseDTO(
                10L, "Biblio Centrale", "12 rue Principale", 48.8566, 2.3522,
                TypeBibliotheque.MUNICIPALE, 4.3, 128, 1.8, true, List.of(h1)
        );

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getNoteGlobale()).isEqualTo(4.3);
        assertThat(dto.getHoraires()).hasSize(1);
        assertThat(dto.getOuvert()).isTrue();
    }

    @Test
    void distanceShouldBeNullable() {
        LibraryResponseDTO dto = new LibraryResponseDTO();
        dto.setDistance(null);
        assertThat(dto.getDistance()).isNull();
    }
}