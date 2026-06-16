package com.bibliotheque.idf.dtos;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import devOps.dtos.FavoriDTO;
import devOps.enums.TypeBibliotheque;

class FavoriDTOTest {

    @Test
    void shouldCreateFavoriWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        FavoriDTO dto = new FavoriDTO(
                7L,
                42L,
                "Médiathèque Jean Jaurès",
                now,
                "12 rue de la Paix, 75001 Paris",
                48.8698,
                2.3309,
                TypeBibliotheque.PUBLIQUE
        );

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getBibliothequeId()).isEqualTo(42L);
        assertThat(dto.getBibliothequeNom()).isEqualTo("Médiathèque Jean Jaurès");
        assertThat(dto.getDateAjout()).isEqualTo(now);
        assertThat(dto.getBibliothequeAdresse()).isEqualTo("12 rue de la Paix, 75001 Paris");
        assertThat(dto.getBibliothequeLatitude()).isEqualTo(48.8698);
        assertThat(dto.getBibliothequeLongitude()).isEqualTo(2.3309);
        assertThat(dto.getBibliothequeType()).isEqualTo(TypeBibliotheque.PUBLIQUE);
    }

    @Test
    void shouldCreateWithNoArgsConstructorAndSetFieldsManually() {
        FavoriDTO dto = new FavoriDTO();

        dto.setId(15L);
        dto.setBibliothequeId(88L);
        dto.setBibliothequeNom("Bibliothèque Universitaire");
        dto.setDateAjout(LocalDateTime.of(2025, 12, 24, 14, 30));
        dto.setBibliothequeAdresse("1 rue Victor Hugo, 75002 Paris");
        dto.setBibliothequeLatitude(48.8566);
        dto.setBibliothequeLongitude(2.3522);
        dto.setBibliothequeType(TypeBibliotheque.UNIVERSITAIRE);

        assertThat(dto.getId()).isEqualTo(15L);
        assertThat(dto.getBibliothequeId()).isEqualTo(88L);
        assertThat(dto.getBibliothequeNom()).isEqualTo("Bibliothèque Universitaire");
        assertThat(dto.getDateAjout().getYear()).isEqualTo(2025);
        assertThat(dto.getDateAjout().getMonthValue()).isEqualTo(12);
        assertThat(dto.getBibliothequeType()).isEqualTo(TypeBibliotheque.UNIVERSITAIRE);
    }

    @Test
    void dateAjoutCanBeNull() {
        FavoriDTO dto = new FavoriDTO(
                3L, 999L, "Petite Biblio", null,
                null, null, null, null
        );
        assertThat(dto.getDateAjout()).isNull();
        assertThat(dto.getBibliothequeAdresse()).isNull();
        assertThat(dto.getBibliothequeType()).isNull();
    }

    @Test
    void shouldHaveCorrectEqualsAndHashCode() {
        LocalDateTime time = LocalDateTime.now();

        FavoriDTO a = new FavoriDTO(1L, 100L, "Bib A", time, "1 rue A", 48.0, 2.0, TypeBibliotheque.PUBLIQUE);
        FavoriDTO b = new FavoriDTO(1L, 100L, "Bib A", time, "1 rue A", 48.0, 2.0, TypeBibliotheque.PUBLIQUE);
        FavoriDTO c = new FavoriDTO(2L, 100L, "Bib A", time, "1 rue A", 48.0, 2.0, TypeBibliotheque.PUBLIQUE);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(c);
    }

    @Test
    void toStringShouldContainMainFields() {
        FavoriDTO dto = new FavoriDTO(
                5L, 77L, "Grande Bibliothèque", LocalDateTime.now(),
                "5 rue de la Bib, 75005 Paris", 48.85, 2.35, TypeBibliotheque.PUBLIQUE
        );
        String str = dto.toString();

        assertThat(str).contains("id=5");
        assertThat(str).contains("bibliothequeId=77");
        assertThat(str).contains("bibliothequeNom=Grande Bibliothèque");
    }
}