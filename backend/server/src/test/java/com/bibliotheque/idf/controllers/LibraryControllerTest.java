package com.bibliotheque.idf.controllers;

import com.bibliotheque.idf.dtos.LibraryResponseDTO;
import com.bibliotheque.idf.services.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires du LibraryController")
class LibraryControllerTest {

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private LibraryController libraryController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Initialise MockMvc avec le controller à tester
        mockMvc = MockMvcBuilders.standaloneSetup(libraryController).build();
    }

    @Test
    @DisplayName("GET /bibliotheques → retourne la liste complète (vide ou non)")
    void getAllBibliotheques_shouldReturnList() throws Exception {
        // Arrange
        when(libraryService.getAllBibliotheques()).thenReturn(List.of());

        // Act via MockMvc (plus réaliste)
        mockMvc.perform(get("/bibliotheques")) // adapte le chemin si différent
                .andExpect(status().isOk());

        // Vérification directe de la méthode (alternative simple)
        ResponseEntity<List<LibraryResponseDTO>> response = libraryController.getAllBibliotheques();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("GET /bibliotheques/{id} → retourne la bibliothèque si elle existe")
    void getBibliothequeById_shouldReturnFoundLibrary() throws Exception {
        // Arrange
        LibraryResponseDTO dto = new LibraryResponseDTO();
        dto.setId(1L);
        dto.setNom("Bibliothèque Nationale");

        when(libraryService.getBibliothequeById(1L)).thenReturn(dto);

        // Act via MockMvc
        mockMvc.perform(get("/bibliotheques/{id}", 1L))
                .andExpect(status().isOk());

        // Vérification directe
        ResponseEntity<LibraryResponseDTO> response = libraryController.getBibliothequeById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getNom()).isEqualTo("Bibliothèque Nationale");
    }

    @Test
    @DisplayName("GET /bibliotheques/{id} → retourne 404 quand la bibliothèque n'existe pas")
    void getBibliothequeById_shouldReturnNotFound() {
        // Arrange
        when(libraryService.getBibliothequeById(999L)).thenReturn(null);

        // Act
        ResponseEntity<LibraryResponseDTO> response = libraryController.getBibliothequeById(999L);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }
}