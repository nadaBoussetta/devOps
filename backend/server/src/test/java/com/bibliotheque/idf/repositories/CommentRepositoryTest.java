package devOps.repositories;

import devOps.models.CommentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository repository;

    @Test
    void findByPostIdOrderByDateCreationAsc_shouldReturnCommentsSortedOldestFirst() {
        Long postId = 100L;

        CommentEntity c1 = createComment(postId, null, 10L, "Premier commentaire", LocalDateTime.now().minusHours(3));
        CommentEntity c2 = createComment(postId, null, 20L, "Deuxième", LocalDateTime.now().minusHours(1));
        CommentEntity c3 = createComment(postId, null, 15L, "Troisième", LocalDateTime.now().minusMinutes(30));

        entityManager.persistAndFlush(c1);
        entityManager.persistAndFlush(c2);
        entityManager.persistAndFlush(c3);

        List<CommentEntity> comments = repository.findByPostIdOrderByDateCreationAsc(postId);

        assertThat(comments).hasSize(3);
        assertThat(comments.get(0).getDateCreation()).isBefore(comments.get(1).getDateCreation());
        assertThat(comments.get(1).getDateCreation()).isBefore(comments.get(2).getDateCreation());
        assertThat(comments.get(0).getContenu()).isEqualTo("Premier commentaire");
    }

    @Test
    void findByParentCommentIdOrderByDateCreationAsc_shouldReturnRepliesSortedOldestFirst() {
        Long postId = 200L;
        CommentEntity parent = createComment(postId, null, 30L, "Commentaire principal", LocalDateTime.now().minusHours(2));
        entityManager.persistAndFlush(parent);

        CommentEntity reply1 = createComment(postId, parent.getId(), 40L, "Réponse 1", LocalDateTime.now().minusMinutes(50));
        CommentEntity reply2 = createComment(postId, parent.getId(), 50L, "Réponse 2", LocalDateTime.now().minusMinutes(10));
        CommentEntity reply3 = createComment(postId, parent.getId(), 35L, "Réponse 3", LocalDateTime.now());

        entityManager.persistAndFlush(reply1);
        entityManager.persistAndFlush(reply2);
        entityManager.persistAndFlush(reply3);

        List<CommentEntity> replies = repository.findByParentCommentIdOrderByDateCreationAsc(parent.getId());

        assertThat(replies).hasSize(3);
        assertThat(replies.get(0).getDateCreation()).isBefore(replies.get(1).getDateCreation());
        assertThat(replies.get(0).getContenu()).isEqualTo("Réponse 1");
    }

    @Test
    void findByAuteurIdOrderByDateCreationDesc_shouldReturnUserCommentsNewestFirst() {
        Long auteurId = 999L;

        CommentEntity c1 = createComment(300L, null, auteurId, "Mon premier", LocalDateTime.now().minusDays(2));
        CommentEntity c2 = createComment(400L, null, auteurId, "Le plus récent", LocalDateTime.now().minusHours(5));
        CommentEntity c3 = createComment(500L, null, auteurId, "Entre les deux", LocalDateTime.now().minusDays(1));

        entityManager.persistAndFlush(c1);
        entityManager.persistAndFlush(c2);
        entityManager.persistAndFlush(c3);

        List<CommentEntity> userComments = repository.findByAuteurIdOrderByDateCreationDesc(auteurId);

        assertThat(userComments).hasSize(3);
        assertThat(userComments.get(0).getDateCreation()).isAfter(userComments.get(1).getDateCreation());
        assertThat(userComments.get(0).getContenu()).isEqualTo("Le plus récent");
    }

    private CommentEntity createComment(Long postId, Long parentId, Long auteurId, String contenu, LocalDateTime date) {
        CommentEntity c = new CommentEntity();
        c.setPostId(postId);
        c.setParentCommentId(parentId);
        c.setAuteurId(auteurId);
        c.setContenu(contenu);
        c.setDateCreation(date);
        return c;
    }
}