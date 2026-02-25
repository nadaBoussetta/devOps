package devOps.repositories;

import devOps.enums.TypeBibliotheque;
import devOps.models.LibraryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LibraryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LibraryRepository repository;

    @Test
    void findByType_shouldReturnOnlyMatchingType() {
        LibraryEntity lib1 = new LibraryEntity();
        lib1.setType(TypeBibliotheque.MUNICIPALE);
        lib1.setNom("Biblio Centre");

        LibraryEntity lib2 = new LibraryEntity();
        lib2.setType(TypeBibliotheque.UNIVERSITAIRE);
        lib2.setNom("BU Sorbonne");

        entityManager.persistAndFlush(lib1);
        entityManager.persistAndFlush(lib2);

        List<LibraryEntity> municipales = repository.findByType(TypeBibliotheque.MUNICIPALE);
        assertThat(municipales).hasSize(1);
        assertThat(municipales.get(0).getNom()).isEqualTo("Biblio Centre");
    }

    @Test
    void findByNomContainingIgnoreCase_shouldBeCaseInsensitive() {
        LibraryEntity lib = new LibraryEntity();
        lib.setNom("Médiathèque Jean Jaurès");
        entityManager.persistAndFlush(lib);

        List<LibraryEntity> results = repository.findByNomContainingIgnoreCase("jean");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getNom()).containsIgnoringCase("Jean");
    }
}