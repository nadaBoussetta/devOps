package devOps.repositories;

import devOps.models.UtilisateurEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UtilisateurRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UtilisateurRepository repository;

    @Test
    void findByUsername_shouldFindUser() {
        UtilisateurEntity user = new UtilisateurEntity();
        user.setUsername("assia_dev");
        user.setEmail("assia@example.com");
        entityManager.persistAndFlush(user);

        Optional<UtilisateurEntity> found = repository.findByUsername("assia_dev");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("assia@example.com");
    }

    @Test
    void existsByEmail_shouldReturnTrueWhenExists() {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setEmail("test@domain.com");
        entityManager.persistAndFlush(u);

        boolean exists = repository.existsByEmail("test@domain.com");
        assertThat(exists).isTrue();
    }
}