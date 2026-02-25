package devOps.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PublicationEntityTest {

    @Test
    void onCreate_shouldSetDateCreation() {
        PublicationEntity post = new PublicationEntity();

        post.onCreate(); // simule @PrePersist

        assertNotNull(post.getDateCreation());
        assertTrue(post.getDateCreation().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void addComment_shouldLinkCommentToPost() {
        PublicationEntity post = new PublicationEntity();
        CommentEntity comment = new CommentEntity();

        post.addComment(comment);

        assertEquals(1, post.getComments().size());
        assertTrue(post.getComments().contains(comment));
        assertEquals(post, comment.getPost());
    }
}