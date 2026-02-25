package devOps.repositories;

import devOps.models.FavoriEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FavoriRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FavoriRepository repository;

    @Test
    void findByUser_IdOrderByDateAjoutDesc_shouldReturnSortedFavorites() {
        FavoriEntity f1 = createFavori(700L, 10L, LocalDateTime.now().minusDays(3));
        FavoriEntity f2 = createFavori(700L, 11L, LocalDateTime.now());

        entityManager.persistAndFlush(f1);
        entityManager.persistAndFlush(f2);

        List<FavoriEntity> favoris = repository.findByUser_IdOrderByDateAjoutDesc(700L);
        assertThat(favoris).hasSize(2);
        assertThat(favoris.get(0).getDateAjout()).isAfter(favoris.get(1).getDateAjout());
    }

    @Test
    void existsByUser_IdAndLibraryEntity_Id_shouldWork() {
        FavoriEntity f = createFavori(800L, 20L, LocalDateTime.now());
        entityManager.persistAndFlush(f);

        boolean exists = repository.existsByUser_IdAndLibraryEntity_Id(800L, 20L);
        assertThat(exists).isTrue();
    }

    private FavoriEntity createFavori(Long userId, Long bibId, LocalDateTime date) {
        FavoriEntity f = new FavoriEntity();
        f.setUserId(userId);           // assuming field is userId or user_Id
        f.setBibliothequeId(bibId);    // adjust field name if different
        f.setDateAjout(date);
        return f;
    }
}