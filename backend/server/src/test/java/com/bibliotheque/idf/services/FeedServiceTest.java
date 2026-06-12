package com.bibliotheque.idf.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import devOps.dtos.CommentDTO;
import devOps.dtos.PublicationDTO;
import devOps.dtos.ReactionDTO;
import devOps.enums.ReactionType;
import devOps.models.CommentEntity;
import devOps.models.LibraryEntity;
import devOps.models.PublicationEntity;
import devOps.models.ReactionEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.CommentRepository;
import devOps.repositories.LibraryRepository;
import devOps.repositories.PublicationRepository;
import devOps.repositories.ReactionRepository;
import devOps.repositories.UtilisateurRepository;
import devOps.services.FeedService;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock private PublicationRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private UtilisateurRepository userRepository;
    @Mock private LibraryRepository bibliothequeRepository;
    @Mock private ReactionRepository reactionRepository;

    @InjectMocks
    private FeedService feedService;

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private UtilisateurEntity makeUser(Long id, String username) {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setId(id);
        u.setUsername(username);
        return u;
    }

    private PublicationEntity makePost(Long id, String contenu, UtilisateurEntity auteur) {
        PublicationEntity p = new PublicationEntity();
        p.setId(id);
        p.setContenu(contenu);
        p.setAuteur(auteur);
        p.setEstRepost(false);
        p.setDateCreation(LocalDateTime.now());
        return p;
    }

    // ─── createPost ───────────────────────────────────────────────────────────

    @Test
    void createPost_shouldCreatePostWithBibliotheque() {
        UtilisateurEntity user = makeUser(1L, "alice");

        LibraryEntity biblio = new LibraryEntity();
        biblio.setId(2L);
        biblio.setNom("Biblio A");

        PublicationDTO input = new PublicationDTO();
        input.setContenu("Mon post");
        input.setBibliothequeId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bibliothequeRepository.findById(2L)).thenReturn(Optional.of(biblio));
        when(postRepository.save(any(PublicationEntity.class))).thenAnswer(inv -> {
            PublicationEntity p = inv.getArgument(0);
            p.setId(99L);
            p.setDateCreation(LocalDateTime.now());
            return p;
        });
        when(reactionRepository.countByTypeForPost(99L)).thenReturn(Collections.emptyList());
        when(reactionRepository.findByUserIdAndPostId(1L, 99L)).thenReturn(Optional.empty());
        when(postRepository.findAllByOrderByDateCreationDesc()).thenReturn(Collections.emptyList());

        PublicationDTO dto = feedService.createPost(input, 1L);

        assertEquals(99L, dto.getId());
        assertEquals("Mon post", dto.getContenu());
        assertEquals(2L, dto.getBibliothequeId());
    }

    @Test
    void createPost_shouldCreatePostWithoutBibliotheque() {
        UtilisateurEntity user = makeUser(1L, "alice");

        PublicationDTO input = new PublicationDTO();
        input.setContenu("Simple post");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(PublicationEntity.class))).thenAnswer(inv -> {
            PublicationEntity p = inv.getArgument(0);
            p.setId(10L);
            p.setDateCreation(LocalDateTime.now());
            return p;
        });
        when(reactionRepository.countByTypeForPost(10L)).thenReturn(Collections.emptyList());
        when(reactionRepository.findByUserIdAndPostId(1L, 10L)).thenReturn(Optional.empty());
        when(postRepository.findAllByOrderByDateCreationDesc()).thenReturn(Collections.emptyList());

        PublicationDTO dto = feedService.createPost(input, 1L);

        assertEquals(10L, dto.getId());
        assertNull(dto.getBibliothequeId());
    }

    // ─── addCommentToPost ─────────────────────────────────────────────────────

    @Test
    void addCommentToPost_shouldReturnCommentDto() {
        UtilisateurEntity user = makeUser(1L, "alice");
        PublicationEntity post = makePost(10L, "contenu", user);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(CommentEntity.class))).thenAnswer(inv -> {
            CommentEntity c = inv.getArgument(0);
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

    // ─── getCommentsByPost ────────────────────────────────────────────────────

    @Test
    void getCommentsByPost_shouldKeepOnlyTopLevelComments() {
        UtilisateurEntity user = makeUser(1L, "alice");
        PublicationEntity post = makePost(10L, "contenu", user);

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

        when(commentRepository.findByPostIdOrderByDateCreationAsc(10L))
                .thenReturn(List.of(parent, reply));

        List<CommentDTO> comments = feedService.getCommentsByPost(10L);

        assertEquals(1, comments.size());
        assertEquals(1L, comments.get(0).getId());
    }

    // ─── réactions ────────────────────────────────────────────────────────────

    @Test
    void reagir_shouldAddNewReaction() {
        UtilisateurEntity user = makeUser(1L, "alice");
        PublicationEntity post = makePost(10L, "contenu", user);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndPostId(1L, 10L)).thenReturn(Optional.empty());
        when(reactionRepository.save(any(ReactionEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reactionRepository.countByTypeForPost(10L)).thenReturn(Collections.emptyList());

        ReactionDTO dto = feedService.reagir(10L, ReactionType.JAIME, 1L);

        assertNotNull(dto);
        assertEquals("JAIME", dto.getMaReaction());
        verify(reactionRepository).save(any(ReactionEntity.class));
    }

    @Test
    void reagir_shouldToggleOffSameReaction() {
        UtilisateurEntity user = makeUser(1L, "alice");
        PublicationEntity post = makePost(10L, "contenu", user);

        ReactionEntity existing = new ReactionEntity();
        existing.setId(5L);
        existing.setUser(user);
        existing.setPost(post);
        existing.setType(ReactionType.JAIME);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndPostId(1L, 10L)).thenReturn(Optional.of(existing));
        when(reactionRepository.countByTypeForPost(10L)).thenReturn(Collections.emptyList());

        ReactionDTO dto = feedService.reagir(10L, ReactionType.JAIME, 1L);

        // Même réaction → suppression (toggle off)
        assertNull(dto.getMaReaction());
        verify(reactionRepository).delete(existing);
    }

    @Test
    void reagir_shouldChangeReactionType() {
        UtilisateurEntity user = makeUser(1L, "alice");
        PublicationEntity post = makePost(10L, "contenu", user);

        ReactionEntity existing = new ReactionEntity();
        existing.setId(5L);
        existing.setUser(user);
        existing.setPost(post);
        existing.setType(ReactionType.JAIME);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndPostId(1L, 10L)).thenReturn(Optional.of(existing));
        when(reactionRepository.save(any(ReactionEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reactionRepository.countByTypeForPost(10L)).thenReturn(Collections.emptyList());

        // Change de JAIME → HAHA
        ReactionDTO dto = feedService.reagir(10L, ReactionType.HAHA, 1L);

        assertEquals("HAHA", dto.getMaReaction());
        verify(reactionRepository).save(existing);
    }

    // ─── repost ───────────────────────────────────────────────────────────────

    @Test
    void repost_shouldCreateRepostWithReference() {
        UtilisateurEntity alice = makeUser(1L, "alice");
        UtilisateurEntity bob   = makeUser(2L, "bob");
        PublicationEntity original = makePost(5L, "Post original", alice);

        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(postRepository.findById(5L)).thenReturn(Optional.of(original));
        when(postRepository.save(any(PublicationEntity.class))).thenAnswer(inv -> {
            PublicationEntity p = inv.getArgument(0);
            p.setId(20L);
            p.setDateCreation(LocalDateTime.now());
            return p;
        });
        when(reactionRepository.countByTypeForPost(20L)).thenReturn(Collections.emptyList());
        when(reactionRepository.findByUserIdAndPostId(2L, 20L)).thenReturn(Optional.empty());
        when(postRepository.findAllByOrderByDateCreationDesc()).thenReturn(Collections.emptyList());

        PublicationDTO dto = feedService.repost(5L, "Mon commentaire", 2L);

        assertTrue(dto.getEstRepost());
        assertEquals(5L, dto.getPostOriginalId());
        assertEquals("alice", dto.getPostOriginalAuteur());
        assertEquals("Post original", dto.getPostOriginalContenu());
    }

    @Test
    void repost_shouldUseOriginalWhenRepostingARepost() {
        UtilisateurEntity alice   = makeUser(1L, "alice");
        UtilisateurEntity bob     = makeUser(2L, "bob");
        UtilisateurEntity charlie = makeUser(3L, "charlie");

        PublicationEntity original = makePost(1L, "Post d'alice", alice);
        PublicationEntity repost1  = makePost(2L, "", bob);
        repost1.setEstRepost(true);
        repost1.setPostOriginal(original);

        when(userRepository.findById(3L)).thenReturn(Optional.of(charlie));
        when(postRepository.findById(2L)).thenReturn(Optional.of(repost1));
        when(postRepository.save(any(PublicationEntity.class))).thenAnswer(inv -> {
            PublicationEntity p = inv.getArgument(0);
            p.setId(30L);
            p.setDateCreation(LocalDateTime.now());
            return p;
        });
        when(reactionRepository.countByTypeForPost(30L)).thenReturn(Collections.emptyList());
        when(reactionRepository.findByUserIdAndPostId(3L, 30L)).thenReturn(Optional.empty());
        when(postRepository.findAllByOrderByDateCreationDesc()).thenReturn(Collections.emptyList());

        PublicationDTO dto = feedService.repost(2L, "", 3L);

        // Charlie reposte le repost de Bob → doit pointer vers le post d'Alice
        assertEquals(1L, dto.getPostOriginalId());
        assertEquals("alice", dto.getPostOriginalAuteur());
    }

    // ─── getAllPosts ──────────────────────────────────────────────────────────

    @Test
    void getAllPosts_shouldReturnEmptyListWhenNoPosts() {
        when(postRepository.findAllByOrderByDateCreationDesc()).thenReturn(Collections.emptyList());

        List<PublicationDTO> posts = feedService.getAllPosts(null);

        assertNotNull(posts);
        assertTrue(posts.isEmpty());
    }
}