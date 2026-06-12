package com.bibliotheque.idf.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import devOps.dtos.PublicationDTO;
import devOps.dtos.ReactionDTO;
import devOps.enums.ReactionType;
import devOps.models.PublicationEntity;
import devOps.models.ReactionEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.CommentRepository;
import devOps.repositories.LibraryRepository;
import devOps.repositories.PublicationRepository;
import devOps.repositories.ReactionRepository;
import devOps.repositories.UtilisateurRepository;
import devOps.services.FeedService;

/**
 * Tests fonctionnels — Feed Social & Réactions
 *
 * TEST 1 : Publier un post → le post apparaît dans le feed
 * TEST 2 : Réagir à un post avec "J'aime" → la réaction est enregistrée
 * TEST 3 : Réagir deux fois avec la même réaction → la réaction est retirée (toggle)
 * TEST 4 : Republier un post (repost) → le repost référence bien le post original
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Feed Social & Réactions — Tests fonctionnels")
class FeedReactionTest {

    @Mock private PublicationRepository postRepository;
    @Mock private CommentRepository     commentRepository;
    @Mock private UtilisateurRepository userRepository;
    @Mock private LibraryRepository     bibliothequeRepository;
    @Mock private ReactionRepository    reactionRepository;

    @InjectMocks
    private FeedService feedService;

    // ── Helpers ───────────────────────────────────────────────────────────────

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

    // ── TEST 1 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TEST 1 — Publier un post : le post apparaît dans le feed")
    void test1_publierPost_apparaitDansLeFeed() {
        // SCÉNARIO : Nour publie "Bonjour tout le monde" et on vérifie
        //            que ce post apparaît bien dans getAllPosts().

        UtilisateurEntity nour = makeUser(1L, "Nour");

        // Le post sauvegardé reçoit un ID = 42
        PublicationEntity postSauvegarde = makePost(42L, "Bonjour tout le monde", nour);

        // Simuler la sauvegarde
        when(userRepository.findById(1L)).thenReturn(Optional.of(nour));
        when(postRepository.save(any(PublicationEntity.class))).thenReturn(postSauvegarde);
        when(reactionRepository.countByTypeForPost(42L)).thenReturn(Collections.emptyList());
        when(reactionRepository.findByUserIdAndPostId(1L, 42L)).thenReturn(Optional.empty());
        when(postRepository.findAllByOrderByDateCreationDesc()).thenReturn(List.of(postSauvegarde));

        // Créer le post
        PublicationDTO input = new PublicationDTO();
        input.setContenu("Bonjour tout le monde");
        feedService.createPost(input, 1L);

        // Vérifier que le post apparaît dans le feed
        List<PublicationDTO> feed = feedService.getAllPosts(1L);

        assertFalse(feed.isEmpty(), "Le feed ne doit pas être vide après publication");
        assertEquals(1, feed.size(), "Le feed doit contenir exactement 1 post");
        assertEquals("Bonjour tout le monde", feed.get(0).getContenu(), "Le contenu doit correspondre");
        assertEquals("Nour", feed.get(0).getAuteurUsername(), "L'auteur doit être Nour");

        System.out.println("✅ TEST 1 PASSÉ — Post publié et visible dans le feed");
    }

    // ── TEST 2 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TEST 2 — Réagir avec J'aime : la réaction est enregistrée")
    void test2_reagirJaime_reactionEnregistree() {
        // SCÉNARIO : Alice réagit avec "J'aime" sur le post de Nour.
        //            On vérifie que la réaction est bien sauvegardée
        //            et que maReaction = "JAIME".

        UtilisateurEntity nour  = makeUser(1L, "Nour");
        UtilisateurEntity alice = makeUser(2L, "Alice");
        PublicationEntity post  = makePost(10L, "Super bibliothèque !", nour);

        // Alice n'a pas encore de réaction sur ce post
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(userRepository.findById(2L)).thenReturn(Optional.of(alice));
        when(reactionRepository.findByUserIdAndPostId(2L, 10L)).thenReturn(Optional.empty());
        when(reactionRepository.save(any(ReactionEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reactionRepository.countByTypeForPost(10L)).thenReturn(Collections.emptyList());

        // Alice réagit avec J'aime
        ReactionDTO result = feedService.reagir(10L, ReactionType.JAIME, 2L);

        // Vérifications
        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals("JAIME", result.getMaReaction(), "La réaction d'Alice doit être JAIME");
        assertNotNull(result.getComptages(), "Les comptages doivent être présents");
        verify(reactionRepository, times(1)).save(any(ReactionEntity.class));

        System.out.println("✅ TEST 2 PASSÉ — Réaction J'aime enregistrée pour Alice");
    }

    // ── TEST 3 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TEST 3 — Double réaction identique : la réaction est retirée (toggle off)")
    void test3_doubleReactionIdentique_reactionRetiree() {
        // SCÉNARIO : Bob a déjà réagi avec "Adorer" sur un post.
        //            Il clique à nouveau sur "Adorer" → la réaction doit être supprimée.

        UtilisateurEntity nour = makeUser(1L, "Nour");
        UtilisateurEntity bob  = makeUser(3L, "Bob");
        PublicationEntity post = makePost(20L, "Belle librairie", nour);

        // Bob a déjà une réaction ADORER sur ce post
        ReactionEntity reactionExistante = new ReactionEntity();
        reactionExistante.setId(99L);
        reactionExistante.setUser(bob);
        reactionExistante.setPost(post);
        reactionExistante.setType(ReactionType.ADORER);

        when(postRepository.findById(20L)).thenReturn(Optional.of(post));
        when(userRepository.findById(3L)).thenReturn(Optional.of(bob));
        when(reactionRepository.findByUserIdAndPostId(3L, 20L)).thenReturn(Optional.of(reactionExistante));
        when(reactionRepository.countByTypeForPost(20L)).thenReturn(Collections.emptyList());

        // Bob re-clique sur ADORER
        ReactionDTO result = feedService.reagir(20L, ReactionType.ADORER, 3L);

        // La réaction doit être supprimée (toggle off)
        assertNull(result.getMaReaction(), "La réaction doit être null après le toggle off");
        verify(reactionRepository, times(1)).delete(reactionExistante);
        verify(reactionRepository, never()).save(any(ReactionEntity.class));

        System.out.println("✅ TEST 3 PASSÉ — Réaction retirée après double clic (toggle off)");
    }

    // ── TEST 4 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TEST 4 — Republier un post : le repost référence le post original")
    void test4_repost_referencePostOriginal() {
        // SCÉNARIO : Nour publie un post. Alice le reposte avec un commentaire.
        //            On vérifie que le repost d'Alice :
        //              - est bien marqué estRepost = true
        //              - référence le post original de Nour
        //              - contient le commentaire d'Alice

        UtilisateurEntity nour  = makeUser(1L, "Nour");
        UtilisateurEntity alice = makeUser(2L, "Alice");
        PublicationEntity postOriginal = makePost(5L, "J'adore cette bibliothèque !", nour);

        // Simuler la sauvegarde du repost
        when(userRepository.findById(2L)).thenReturn(Optional.of(alice));
        when(postRepository.findById(5L)).thenReturn(Optional.of(postOriginal));
        when(postRepository.save(any(PublicationEntity.class))).thenAnswer(inv -> {
            PublicationEntity repost = inv.getArgument(0);
            repost.setId(55L);
            repost.setDateCreation(LocalDateTime.now());
            return repost;
        });
        when(reactionRepository.countByTypeForPost(55L)).thenReturn(Collections.emptyList());
        when(reactionRepository.findByUserIdAndPostId(2L, 55L)).thenReturn(Optional.empty());
        when(postRepository.findAllByOrderByDateCreationDesc()).thenReturn(Collections.emptyList());

        // Alice reposte avec un commentaire
        PublicationDTO repost = feedService.repost(5L, "Je partage cet avis !", 2L);

        // Vérifications
        assertNotNull(repost, "Le repost ne doit pas être null");
        assertTrue(repost.getEstRepost(), "Le post doit être marqué comme repost");
        assertEquals(5L, repost.getPostOriginalId(), "L'ID du post original doit être 5");
        assertEquals("Nour", repost.getPostOriginalAuteur(), "L'auteur original doit être Nour");
        assertEquals("J'adore cette bibliothèque !", repost.getPostOriginalContenu(), "Le contenu original doit être préservé");
        assertEquals("Je partage cet avis !", repost.getContenu(), "Le commentaire d'Alice doit être dans le repost");

        System.out.println("✅ TEST 4 PASSÉ — Repost créé avec référence au post original de Nour");
    }
}