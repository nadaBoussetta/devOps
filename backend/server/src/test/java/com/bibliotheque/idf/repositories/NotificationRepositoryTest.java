package devOps.repositories;

import devOps.enums.TypeNotification;
import devOps.models.NotificationEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository repository;

    @Test
    void findByUserIdAndLueIsFalseOrderByDateCreationDesc_shouldReturnUnreadOnly() {
        NotificationEntity n1 = createNotification(10L, TypeNotification.NOUVELLE_REPONSE, false, LocalDateTime.now().minusHours(2));
        NotificationEntity n2 = createNotification(10L, TypeNotification.NOUVELLE_SESSION, true, LocalDateTime.now().minusHours(1));
        NotificationEntity n3 = createNotification(10L, TypeNotification.AVIS_AJOUTE, false, LocalDateTime.now());

        entityManager.persistAndFlush(n1);
        entityManager.persistAndFlush(n2);
        entityManager.persistAndFlush(n3);

        List<NotificationEntity> unread = repository.findByUserIdAndLueIsFalseOrderByDateCreationDesc(10L);

        assertThat(unread).hasSize(2);
        assertThat(unread.get(0).getDateCreation()).isAfter(unread.get(1).getDateCreation());
        assertThat(unread).allMatch(n -> !n.getLue());
    }

    @Test
    void countByUserIdAndLueIsFalse_shouldCountUnreadCorrectly() {
        createNotification(20L, TypeNotification.COMMENTAIRE, false, LocalDateTime.now());
        createNotification(20L, TypeNotification.COMMENTAIRE, false, LocalDateTime.now().minusMinutes(10));
        createNotification(20L, TypeNotification.COMMENTAIRE, true, LocalDateTime.now());

        int count = repository.countByUserIdAndLueIsFalse(20L);
        assertThat(count).isEqualTo(2);
    }

    private NotificationEntity createNotification(Long userId, TypeNotification type, boolean lue, LocalDateTime date) {
        NotificationEntity n = new NotificationEntity();
        n.setUserId(userId);
        n.setType(type);
        n.setLue(lue);
        n.setDateCreation(date);
        return n;
    }
}