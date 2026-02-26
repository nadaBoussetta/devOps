package com.bibliotheque.idf.controllers;

import com.bibliotheque.idf.dtos.LivreResponseDTO; // adapte le nom si différent
import com.bibliotheque.idf.services.LivreService;
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
@DisplayName("Tests du LivreController")
class LivreControllerTest {

    @Mock
    private LivreService livreService;

    @InjectMocks
    private LivreController livreController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(livreController).build();
    }

    @Test
    @DisplayName("GET /livres/recherche?query=java → retourne liste vide → 200 OK")
    void rechercherLivre_shouldReturnEmptyListWhenNoResults() throws Exception {
        // Arrange
        String query = "java";
        when(livreService.rechercherLivre(query)).thenReturn(List.of());

        // Act via MockMvc (recommandé pour tester les controllers)
        mockMvc.perform(get("/livres/recherche")
                        .param("query", query))
                .andExpect(status().isOk());

        // Vérification directe de la méthode (alternative simple)
        ResponseEntity<List<LivreResponseDTO>> response = livreController.rechercherLivre(query);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("GET /livres/recherche?query=java → retourne des résultats → 200 OK")
    void rechercherLivre_shouldReturnResults() {
        // Arrange
        String query = "java";
        LivreResponseDTO livre1 = new LivreResponseDTO();
        livre1.setTitre("Java pour les nuls");
        LivreResponseDTO livre2 = new LivreResponseDTO();
        livre2.setTitre("Effective Java");

        when(livreService.rechercherLivre(query)).thenReturn(List.of(livre1, livre2));

        // Act
        ResponseEntity<List<LivreResponseDTO>> response = livreController.rechercherLivre(query);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody())
                .isNotNull()
                .hasSize(2)
                .extracting(LivreResponseDTO::getTitre)
                .containsExactly("Java pour les nuls", "Effective Java");
    }

    @Test
    @DisplayName("GET /livres/recherche?query=xxx → service retourne null → 200 avec liste vide")
    void rechercherLivre_shouldHandleNullFromService() {
        // Arrange
        String query = "xxx";
        when(livreService.rechercherLivre(query)).thenReturn(null);

        // Act
        ResponseEntity<List<LivreResponseDTO>> response = livreController.rechercherLivre(query);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().isEmpty();
    }
}