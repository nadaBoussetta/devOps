package com.bibliotheque.idf.controllers;

import com.bibliotheque.idf.dtos.PublicationDTO;
import com.bibliotheque.idf.services.FeedService;
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
@DisplayName("Tests du FeedController")
class FeedControllerTest {

    @Mock
    private FeedService feedService;

    @InjectMocks
    private FeedController feedController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(feedController).build();
    }

    @Test
    @DisplayName("GET /feeds → retourne tous les posts (liste vide)")
    void getAllPosts_shouldReturnEmptyList() throws Exception {
        // Arrange
        when(feedService.getAllPosts()).thenReturn(List.of());

        // Act & Assert via MockMvc
        mockMvc.perform(get("/feeds")) // adapte le chemin si différent
                .andExpect(status().isOk());

        // Vérification directe si tu préfères appeler la méthode
        ResponseEntity<List<PublicationDTO>> response = feedController.getAllPosts();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("GET /feeds/{id} → retourne un post existant")
    void getPostById_shouldReturnPost() throws Exception {
        // Arrange
        PublicationDTO dto = new PublicationDTO();
        dto.setId(1L);
        dto.setTitle("Test Post");

        when(feedService.getPostById(1L)).thenReturn(dto);

        // Act & Assert via MockMvc
        mockMvc.perform(get("/feeds/{id}", 1L))
                .andExpect(status().isOk());

        // Vérification directe
        ResponseEntity<PublicationDTO> response = feedController.getPostById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getTitle()).isEqualTo("Test Post");
    }

    @Test
    @DisplayName("GET /feeds/{id} → retourne 404 quand le post n'existe pas")
    void getPostById_shouldReturnNotFound() {
        // Arrange
        when(feedService.getPostById(999L)).thenReturn(null);

        // Act
        ResponseEntity<PublicationDTO> response = feedController.getPostById(999L);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }
}