package com.bibliotheque.idf.services;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotationServiceTest {

    @Mock
    private NotationRepository notationRepository;

    @Mock
    private FavoriRepository favoriRepository;

    @Mock
    private UtilisateurRepository userRepository;

    @Mock
    private LibraryRepository bibliothequeRepository;

    @InjectMocks
    private NotationService notationService;

    @Test
    void noterBibliotheque_shouldSaveNotationAndUpdateLibraryAverage() {
        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(1L);

        LibraryEntity biblio = new LibraryEntity();
        biblio.setId(2L);
        biblio.setNom("Biblio A");

        NotationDTO input = new NotationDTO();
        input.setBibliothequeId(2L);
        input.setNote(4);
        input.setCommentaire("Calme");
        input.setDateVisite(LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bibliothequeRepository.findById(2L)).thenReturn(Optional.of(biblio));
        when(notationRepository.findByUserIdAndBibliothequeId(1L, 2L)).thenReturn(Optional.empty());
        when(notationRepository.save(any(NotationEntity.class))).thenAnswer(invocation -> {
            NotationEntity n = invocation.getArgument(0);
            n.setId(99L);
            n.setDateNotation(LocalDate.now());
            return n;
        });

        NotationEntity oldNotation = new NotationEntity();
        oldNotation.setNote(2);
        oldNotation.setBibliotheque(biblio);

        NotationEntity newNotation = new NotationEntity();
        newNotation.setNote(4);
        newNotation.setBibliotheque(biblio);

        when(notationRepository.findByBibliothequeIdOrderByDateNotationDesc(2L)).thenReturn(List.of(newNotation, oldNotation));

        NotationDTO dto = notationService.noterBibliotheque(input, 1L);

        assertEquals(99L, dto.getId());
        assertEquals(4, dto.getNote());
        verify(bibliothequeRepository).save(biblio);
        assertEquals(3.0, biblio.getNoteGlobale());
        assertEquals(2, biblio.getNombreNotations());
    }

    @Test
    void ajouterFavori_shouldThrowWhenAlreadyFavorite() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new UtilisateurEntity()));
        when(bibliothequeRepository.findById(2L)).thenReturn(Optional.of(new LibraryEntity()));
        when(favoriRepository.existsByUser_IdAndLibraryEntity_Id(1L, 2L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> notationService.ajouterFavori(2L, 1L));
    }

    @Test
    void getFavorisByUser_shouldMapFavoris() {
        LibraryEntity biblio = new LibraryEntity();
        biblio.setId(5L);
        biblio.setNom("Biblio Favorite");

        FavoriEntity favori = new FavoriEntity();
        favori.setId(10L);
        favori.setLibraryEntity(biblio);
        favori.setDateAjout(LocalDateTime.now());

        when(favoriRepository.findByUser_IdOrderByDateAjoutDesc(1L)).thenReturn(List.of(favori));

        List<FavoriDTO> favoris = notationService.getFavorisByUser(1L);

        assertEquals(1, favoris.size());
        assertEquals(5L, favoris.get(0).getBibliothequeId());
    }
}