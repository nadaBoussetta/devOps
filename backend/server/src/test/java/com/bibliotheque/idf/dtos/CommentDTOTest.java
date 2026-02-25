package devOps.dtos;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentDTO_Test {

    @Test
    void shouldSupportNestedReplies() {
        CommentDTO reply1 = new CommentDTO(101L, "Super réponse !", 2L, "alice", 50L, null, LocalDateTime.now(), null);
        CommentDTO comment = new CommentDTO(50L, "Excellent livre", 1L, "john", 10L, null, LocalDateTime.now().minusDays(1), List.of(reply1));

        assertThat(comment.getReponses()).hasSize(1);
        assertThat(comment.getReponses().get(0).getContenu()).isEqualTo("Super réponse !");
        assertThat(comment.getDateCreation()).isBefore(reply1.getDateCreation());
    }
}