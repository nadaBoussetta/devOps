package devOps.repositories;

import devOps.models.SessionEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SessionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SessionRepository repository;

    @Test
    void findByUserIdAndCompleteeIsFalse_shouldFindCurrentSession() {
        SessionEntity s1 = new SessionEntity();
        s1.setUserId(500L);
        s1.setCompletee(false);
        s1.setDateCreation(LocalDateTime.now().minusHours(1));

        SessionEntity s2 = new SessionEntity();
        s2.setUserId(500L);
        s2.setCompletee(true);
        s2.setDateCreation(LocalDateTime.now().minusDays(1));

        entityManager.persistAndFlush(s1);
        entityManager.persistAndFlush(s2);

        Optional<SessionEntity> current = repository.findByUserIdAndCompleteeIsFalse(500L);
        assertThat(current).isPresent();
        assertThat(current.get().getCompletee()).isFalse();
    }

    @Test
    void countByUserIdAndCompleteeIsTrue_shouldCountCompleted() {
        for (int i = 0; i < 3; i++) {
            SessionEntity s = new SessionEntity();
            s.setUserId(600L);
            s.setCompletee(true);
            entityManager.persist(s);
        }
        entityManager.flush();

        int count = repository.countByUserIdAndCompleteeIsTrue(600L);
        assertThat(count).isEqualTo(3);
    }
}