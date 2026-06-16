package com.bibliotheque.idf.controllers;

import devOps.controllers.FavoriController;
import devOps.dtos.FavoriDTO;
import devOps.repositories.FavoriRepository;
import devOps.repositories.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriControllerTest {

    private FavoriController favoriController;
    private FavoriRepository favoriRepository;
    private LibraryRepository libraryRepository;

    @BeforeEach
    void setUp() throws Exception {
        favoriController = new FavoriController();

        favoriRepository = mock(FavoriRepository.class);
        libraryRepository = mock(LibraryRepository.class);

        inject("favoriRepository", favoriRepository);
        inject("libraryRepository", libraryRepository);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field field = FavoriController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(favoriController, value);
    }

    @Test
    void ajouterAuxFavoris_shouldThrowWhenLibraryNotFound() {
        FavoriDTO favoriDTO = new FavoriDTO();
        favoriDTO.setBibliothequeId(1L);

        when(libraryRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> favoriController.ajouterAuxFavoris(favoriDTO)
        );

        assertEquals("Bibliothèque non trouvée", exception.getMessage());

        verify(libraryRepository).findById(1L);
        verify(favoriRepository, never()).save(any());
    }
}