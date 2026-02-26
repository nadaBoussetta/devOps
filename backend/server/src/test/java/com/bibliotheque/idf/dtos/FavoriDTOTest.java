package com.bibliotheque.idf.dtos;

import devOps.dtos.FavoriDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FavoriDTOTest {

    @Test
    void shouldCreateFavoriWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        
        FavoriDTO dto = new FavoriDTO(
                7L,
                42L,
                "Médiathèque Jean Jaurès",
                now
        );

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getBibliothequeId()).isEqualTo(42L);
        assertThat(dto.getBibliothequeNom()).isEqualTo("Médiathèque Jean Jaurès");
        assertThat(dto.getDateAjout()).isEqualTo(now);
    }

    @Test
    void shouldCreateWithNoArgsConstructorAndSetFieldsManually() {
        FavoriDTO dto = new FavoriDTO();
        
        dto.setId(15L);
        dto.setBibliothequeId(88L);
        dto.setBibliothequeNom("Bibliothèque Universitaire");
        dto.setDateAjout(LocalDateTime.of(2025, 12, 24, 14, 30));

        assertThat(dto.getId()).isEqualTo(15L);
        assertThat(dto.getBibliothequeId()).isEqualTo(88L);
        assertThat(dto.getBibliothequeNom()).isEqualTo("Bibliothèque Universitaire");
        assertThat(dto.getDateAjout().getYear()).isEqualTo(2025);
        assertThat(dto.getDateAjout().getMonthValue()).isEqualTo(12);
    }

    @Test
    void dateAjoutCanBeNull() {
        FavoriDTO dto = new FavoriDTO(3L, 999L, "Petite Biblio", null);
        assertThat(dto.getDateAjout()).isNull();
    }

    @Test
    void shouldHaveCorrectEqualsAndHashCode() {
        LocalDateTime time = LocalDateTime.now();
        
        FavoriDTO a = new FavoriDTO(1L, 100L, "Bib A", time);
        FavoriDTO b = new FavoriDTO(1L, 100L, "Bib A", time);
        FavoriDTO c = new FavoriDTO(2L, 100L, "Bib A", time);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    void toStringShouldContainMainFields() {
        FavoriDTO dto = new FavoriDTO(5L, 77L, "Grande Bibliothèque", LocalDateTime.now());
        String str = dto.toString();

        assertThat(str).contains("id=5");
        assertThat(str).contains("bibliothequeId=77");
        assertThat(str).contains("bibliothequeNom=Grande Bibliothèque");
    }
}