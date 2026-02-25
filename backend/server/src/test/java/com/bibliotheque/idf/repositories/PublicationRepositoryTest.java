package devOps.repositories;

import devOps.models.PublicationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PublicationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PublicationRepository repository;

    @Test
    void findByAuteurId_shouldReturnAllPublicationsFromUser() {
        Long auteurId = 777L;

        PublicationEntity p1 = createPublication(auteurId, "Post 1", LocalDateTime.now().minusDays(3));
        PublicationEntity p2 = createPublication(auteurId, "Post 2", LocalDateTime.now().minusHours(12));
        PublicationEntity p3 = createPublication(888L, "Post autre utilisateur", LocalDateTime.now());

        entityManager.persistAndFlush(p1);
        entityManager.persistAndFlush(p2);
        entityManager.persistAndFlush(p3);

        List<PublicationEntity> userPosts = repository.findByAuteurId(auteurId);

        assertThat(userPosts).hasSize(2);
        assertThat(userPosts).extracting(PublicationEntity::getAuteurId).containsOnly(auteurId);
    }

    @Test
    void findByAuteurIdOrderByDateCreationDesc_shouldReturnSortedNewestFirst() {
        Long auteurId = 666L;

        PublicationEntity p1 = createPublication(auteurId, "Ancien", LocalDateTime.now().minusDays(5));
        PublicationEntity p2 = createPublication(auteurId, "Récent", LocalDateTime.now().minusHours(2));
        PublicationEntity p3 = createPublication(auteurId, "Moyen", LocalDateTime.now().minusDays(1));

        entityManager.persistAndFlush(p1);
        entityManager.persistAndFlush(p2);
        entityManager.persistAndFlush(p3);

        List<PublicationEntity> sorted = repository.findByAuteurIdOrderByDateCreationDesc(auteurId);

        assertThat(sorted).hasSize(3);
        assertThat(sorted.get(0).getDateCreation()).isAfter(sorted.get(1).getDateCreation());
        assertThat(sorted.get(0).getContenu()).isEqualTo("Récent");
    }

    @Test
    void findAllByOrderByDateCreationDesc_shouldReturnAllPublicationsNewestFirst() {
        PublicationEntity p1 = createPublication(100L, "Premier", LocalDateTime.now().minusDays(10));
        PublicationEntity p2 = createPublication(200L, "Dernier ajouté", LocalDateTime.now());
        PublicationEntity p3 = createPublication(300L, "Milieu", LocalDateTime.now().minusHours(8));

        entityManager.persistAndFlush(p1);
        entityManager.persistAndFlush(p2);
        entityManager.persistAndFlush(p3);

        List<PublicationEntity> allSorted = repository.findAllByOrderByDateCreationDesc();

        assertThat(allSorted).hasSize(3);
        assertThat(allSorted.get(0).getDateCreation()).isAfter(allSorted.get(1).getDateCreation());
        assertThat(allSorted.get(0).getContenu()).isEqualTo("Dernier ajouté");
    }

    private PublicationEntity createPublication(Long auteurId, String contenu, LocalDateTime date) {
        PublicationEntity p = new PublicationEntity();
        p.setAuteurId(auteurId);
        p.setContenu(contenu);
        p.setDateCreation(date);
        // ajoutez d'autres champs obligatoires si nécessaire (ex: bibliothequeId, etc.)
        return p;
    }
}