package devOps.repositories;

import devOps.models.NotationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotationRepository repository;

    @Test
    void findByUserIdOrderByDateNotationDesc_shouldReturnSortedList() {
        NotationEntity n1 = new NotationEntity();
        n1.setUserId(100L);
        n1.setBibliothequeId(42L);
        n1.setNote(4);
        n1.setDateNotation(LocalDate.now().minusDays(5));

        NotationEntity n2 = new NotationEntity();
        n2.setUserId(100L);
        n2.setBibliothequeId(43L);
        n2.setNote(5);
        n2.setDateNotation(LocalDate.now());

        entityManager.persistAndFlush(n1);
        entityManager.persistAndFlush(n2);

        List<NotationEntity> results = repository.findByUserIdOrderByDateNotationDesc(100L);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getDateNotation()).isAfter(results.get(1).getDateNotation());
    }

    @Test
    void findByUserIdAndBibliothequeId_shouldFindExistingNotation() {
        NotationEntity notation = new NotationEntity();
        notation.setUserId(200L);
        notation.setBibliothequeId(50L);
        notation.setNote(3);
        entityManager.persistAndFlush(notation);

        Optional<NotationEntity> found = repository.findByUserIdAndBibliothequeId(200L, 50L);
        assertThat(found).isPresent();
        assertThat(found.get().getNote()).isEqualTo(3);
    }

    @Test
    void existsByUserIdAndBibliothequeId_shouldReturnTrueWhenExists() {
        NotationEntity n = new NotationEntity();
        n.setUserId(300L);
        n.setBibliothequeId(60L);
        entityManager.persistAndFlush(n);

        boolean exists = repository.existsByUserIdAndBibliothequeId(300L, 60L);
        assertThat(exists).isTrue();
    }
}