package devOps.services;

import devOps.dtos.CommentDTO;
import devOps.dtos.PublicationDTO;
import devOps.models.CommentEntity;
import devOps.models.LibraryEntity;
import devOps.models.PublicationEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.CommentRepository;
import devOps.repositories.LibraryRepository;
import devOps.repositories.PublicationRepository;
import devOps.repositories.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private PublicationRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UtilisateurRepository userRepository;

    @Mock
    private LibraryRepository bibliothequeRepository;

    @InjectMocks
    private FeedService feedService;

    @Test
    void createPost_shouldCreatePostWithBibliotheque() {
        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(1L);
        user.setUsername("alice");

        LibraryEntity biblio = new LibraryEntity();
        biblio.setId(2L);
        biblio.setNom("Biblio A");

        PublicationDTO input = new PublicationDTO();
        input.setContenu("Mon post");
        input.setBibliothequeId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bibliothequeRepository.findById(2L)).thenReturn(Optional.of(biblio));
        when(postRepository.save(any(PublicationEntity.class))).thenAnswer(invocation -> {
            PublicationEntity p = invocation.getArgument(0);
            p.setId(99L);
            p.setDateCreation(LocalDateTime.now());
            return p;
        });

        PublicationDTO dto = feedService.createPost(input, 1L);

        assertEquals(99L, dto.getId());
        assertEquals("Mon post", dto.getContenu());
        assertEquals(2L, dto.getBibliothequeId());
    }

    @Test
    void addCommentToPost_shouldReturnCommentDto() {
        PublicationEntity post = new PublicationEntity();
        post.setId(10L);

        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(1L);
        user.setUsername("alice");

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(CommentEntity.class))).thenAnswer(invocation -> {
            CommentEntity c = invocation.getArgument(0);
            c.setId(5L);
            c.setDateCreation(LocalDateTime.now());
            return c;
        });

        CommentDTO input = new CommentDTO();
        input.setContenu("Super");

        CommentDTO dto = feedService.addCommentToPost(10L, input, 1L);

        assertEquals(5L, dto.getId());
        assertEquals("Super", dto.getContenu());
        assertEquals("alice", dto.getAuteurUsername());
    }

    @Test
    void getCommentsByPost_shouldKeepOnlyTopLevelComments() {
        PublicationEntity post = new PublicationEntity();
        post.setId(10L);

        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(1L);
        user.setUsername("alice");

        CommentEntity parent = new CommentEntity();
        parent.setId(1L);
        parent.setContenu("Parent");
        parent.setAuteur(user);
        parent.setPost(post);
        parent.setDateCreation(LocalDateTime.now());

        CommentEntity reply = new CommentEntity();
        reply.setId(2L);
        reply.setContenu("Reply");
        reply.setAuteur(user);
        reply.setPost(post);
        reply.setParentComment(parent);
        reply.setDateCreation(LocalDateTime.now());

        when(commentRepository.findByPostIdOrderByDateCreationAsc(10L)).thenReturn(List.of(parent, reply));

        List<CommentDTO> comments = feedService.getCommentsByPost(10L);

        assertEquals(1, comments.size());
        assertEquals(1L, comments.get(0).getId());
    }
}