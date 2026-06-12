package com.bibliotheque.idf.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import devOps.dtos.FavoriDTO;
import devOps.dtos.NotationDTO;
import devOps.models.FavoriEntity;
import devOps.models.LibraryEntity;
import devOps.models.NotationEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.FavoriRepository;
import devOps.repositories.LibraryRepository;
import devOps.repositories.NotationRepository;
import devOps.repositories.UtilisateurRepository;
import devOps.services.NotationService;

/**
 * Tests — Compteurs Favoris & Avis affichés sur le profil utilisateur
 *
 * TEST 1 : nbFavoris affiché sur le profil = nombre réel de favoris
 * TEST 2 : nbFavoris diminue après suppression d'un favori
 * TEST 3 : nbAvis affiché sur le profil = nombre réel de notations
 * TEST 4 : Un utilisateur sans favoris ni avis affiche 0 partout
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Profil — Compteurs Favoris & Avis")
class ProfileCountersTest {

    @Mock private NotationRepository    notationRepository;
    @Mock private FavoriRepository      favoriRepository;
    @Mock private UtilisateurRepository userRepository;
    @Mock private LibraryRepository     bibliothequeRepository;

    @InjectMocks
    private NotationService notationService;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UtilisateurEntity makeUser(Long id, String username) {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setId(id);
        u.setUsername(username);
        return u;
    }

    private LibraryEntity makeBiblio(Long id, String nom) {
        LibraryEntity b = new LibraryEntity();
        b.setId(id);
        b.setNom(nom);
        return b;
    }

    private FavoriEntity makeFavori(Long id, UtilisateurEntity user, LibraryEntity biblio) {
        FavoriEntity f = new FavoriEntity();
        f.setId(id);
        f.setUser(user);
        f.setLibraryEntity(biblio);
        f.setDateAjout(LocalDateTime.now());
        return f;
    }

    private NotationEntity makeNotation(Long id, UtilisateurEntity user, LibraryEntity biblio, int note) {
        NotationEntity n = new NotationEntity();
        n.setId(id);
        n.setUser(user);
        n.setBibliotheque(biblio);
        n.setNote(note);
        n.setCommentaire("Très bien");
        n.setDateVisite(LocalDate.now());
        n.setDateNotation(LocalDate.now());
        return n;
    }

    // ── TEST 1 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TEST 1 — nbFavoris du profil = nombre réel de bibliothèques favorites")
    void test1_nbFavoris_correspondAuNombreReelDeFavoris() {
        // SCÉNARIO : Nour a ajouté 3 bibliothèques en favoris.
        //            Le profil doit afficher nbFavoris = 3.

        UtilisateurEntity nour = makeUser(1L, "Nour");
        LibraryEntity bib1 = makeBiblio(1L, "BnF");
        LibraryEntity bib2 = makeBiblio(2L, "Mazarine");
        LibraryEntity bib3 = makeBiblio(3L, "Sainte-Geneviève");

        List<FavoriEntity> favorisNour = List.of(
                makeFavori(1L, nour, bib1),
                makeFavori(2L, nour, bib2),
                makeFavori(3L, nour, bib3)
        );

        when(favoriRepository.findByUser_IdOrderByDateAjoutDesc(1L)).thenReturn(favorisNour);

        // Récupérer les favoris comme le fait UserController.getProfile()
        List<FavoriDTO> favoris = notationService.getFavorisByUser(1L);
        int nbFavoris = favoris.size();

        // Vérifications
        assertEquals(3, nbFavoris,
                "Le profil de Nour doit afficher 3 favoris");
        assertEquals("BnF",               favoris.get(0).getBibliothequeNom());
        assertEquals("Mazarine",          favoris.get(1).getBibliothequeNom());
        assertEquals("Sainte-Geneviève",  favoris.get(2).getBibliothequeNom());

        System.out.println("✅ TEST 1 PASSÉ — nbFavoris = " + nbFavoris);
    }

    // ── TEST 2 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TEST 2 — nbFavoris diminue après suppression d'un favori")
    void test2_suppressionFavori_nbFavorisDiminue() {
        // SCÉNARIO : Nour a 3 favoris. Elle en supprime 1.
        //            Le profil doit ensuite afficher nbFavoris = 2.

        UtilisateurEntity nour = makeUser(1L, "Nour");
        LibraryEntity bib1 = makeBiblio(1L, "BnF");
        LibraryEntity bib2 = makeBiblio(2L, "Mazarine");
        LibraryEntity bib3 = makeBiblio(3L, "Sainte-Geneviève");

        // Avant suppression : 3 favoris
        when(favoriRepository.findByUser_IdOrderByDateAjoutDesc(1L))
                .thenReturn(List.of(
                        makeFavori(1L, nour, bib1),
                        makeFavori(2L, nour, bib2),
                        makeFavori(3L, nour, bib3)
                ))
                // Après suppression de bib3 : 2 favoris
                .thenReturn(List.of(
                        makeFavori(1L, nour, bib1),
                        makeFavori(2L, nour, bib2)
                ));

        doNothing().when(favoriRepository).deleteByUser_IdAndLibraryEntity_Id(1L, 3L);

        // Avant suppression
        int nbAvant = notationService.getFavorisByUser(1L).size();
        assertEquals(3, nbAvant, "Avant suppression : 3 favoris");

        // Supprimer bib3
        notationService.supprimerFavori(3L, 1L);

        // Après suppression
        int nbApres = notationService.getFavorisByUser(1L).size();
        assertEquals(2, nbApres, "Après suppression : 2 favoris");
        assertEquals(nbAvant - 1, nbApres, "Le compteur doit diminuer de 1");

        verify(favoriRepository, times(1)).deleteByUser_IdAndLibraryEntity_Id(1L, 3L);

        System.out.println("✅ TEST 2 PASSÉ — nbFavoris : " + nbAvant + " → " + nbApres + " après suppression");
    }

    // ── TEST 3 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TEST 3 — nbAvis du profil = nombre réel de notations laissées")
    void test3_nbAvis_correspondAuNombreReelDeNotations() {
        // SCÉNARIO : Nour a noté 2 bibliothèques (4/5 et 3/5).
        //            Le profil doit afficher nbAvis = 2.

        UtilisateurEntity nour = makeUser(1L, "Nour");
        LibraryEntity bib1 = makeBiblio(1L, "BnF");
        LibraryEntity bib2 = makeBiblio(2L, "Mazarine");

        List<NotationEntity> notationsNour = List.of(
                makeNotation(1L, nour, bib1, 4),
                makeNotation(2L, nour, bib2, 3)
        );

        when(notationRepository.findByUserIdOrderByDateNotationDesc(1L)).thenReturn(notationsNour);

        // Récupérer les avis comme le fait UserController.getProfile()
        List<NotationDTO> avis = notationService.getNotationsByUser(1L);
        int nbAvis = avis.size();

        // Vérifications
        assertEquals(2, nbAvis,
                "Le profil de Nour doit afficher 2 avis");
        assertEquals(4, avis.get(0).getNote(),
                "Premier avis : note = 4");
        assertEquals(3, avis.get(1).getNote(),
                "Deuxième avis : note = 3");

        System.out.println("✅ TEST 3 PASSÉ — nbAvis = " + nbAvis);
    }

    // ── TEST 4 ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TEST 4 — Nouvel utilisateur : tous les compteurs du profil affichent 0")
    void test4_nouvelUtilisateur_tousLesCompteursASont0() {
        // SCÉNARIO : Charlie vient de s'inscrire.
        //            Il n'a aucun favori, aucun avis.
        //            Tous les compteurs du profil doivent être à 0.

        when(favoriRepository.findByUser_IdOrderByDateAjoutDesc(99L))
                .thenReturn(List.of());
        when(notationRepository.findByUserIdOrderByDateNotationDesc(99L))
                .thenReturn(List.of());

        List<FavoriDTO>  favoris = notationService.getFavorisByUser(99L);
        List<NotationDTO> avis   = notationService.getNotationsByUser(99L);

        // Simuler les compteurs du profil
        int nbFavoris = favoris.size();
        int nbAvis    = avis.size();

        assertEquals(0, nbFavoris, "nbFavoris doit être 0 pour un nouvel utilisateur");
        assertEquals(0, nbAvis,    "nbAvis doit être 0 pour un nouvel utilisateur");

        assertTrue(favoris.isEmpty(), "La liste des favoris doit être vide");
        assertTrue(avis.isEmpty(),    "La liste des avis doit être vide");

        System.out.println("✅ TEST 4 PASSÉ — Nouvel utilisateur : nbFavoris=0, nbAvis=0");
    }
}