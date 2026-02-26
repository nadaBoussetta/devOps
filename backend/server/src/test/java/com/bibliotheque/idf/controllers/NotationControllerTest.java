package com.bibliotheque.idf.controllers;  // ou devOps.controllers selon ton package

import com.bibliotheque.idf.dtos.NotationResponseDTO; // adapte le nom exact du DTO si différent
import com.bibliotheque.idf.services.NotationService;
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
@DisplayName("Tests du NotationController")
class NotationControllerTest {

    @Mock
    private NotationService notationService;

    @InjectMocks
    private NotationController notationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notationController).build();
    }

    @Test
    @DisplayName("GET /notations/bibliotheque/{id} → liste vide → 200 OK")
    void getNotationsByBibliotheque_emptyList_shouldReturn200() throws Exception {
        // Arrange
        Long bibliothequeId = 1L;
        when(notationService.getNotationsByBibliotheque(bibliothequeId)).thenReturn(List.of());

        // Act via MockMvc (meilleure pratique)
        mockMvc.perform(get("/notations/bibliotheque/{id}", bibliothequeId))
                .andExpect(status().isOk());

        // Vérification directe (alternative simple)
        ResponseEntity<List<NotationResponseDTO>> response = 
                notationController.getNotationsByBibliotheque(bibliothequeId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("GET /notations/bibliotheque/{id} → notations existantes → 200 + liste non vide")
    void getNotationsByBibliotheque_withResults_shouldReturnNotations() {
        // Arrange
        Long bibliothequeId = 1L;
        NotationResponseDTO n1 = new NotationResponseDTO();
        n1.setNote(4.5);
        NotationResponseDTO n2 = new NotationResponseDTO();
        n2.setNote(3.0);

        when(notationService.getNotationsByBibliotheque(bibliothequeId))
                .thenReturn(List.of(n1, n2));

        // Act
        ResponseEntity<List<NotationResponseDTO>> response = 
                notationController.getNotationsByBibliotheque(bibliothequeId);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody())
                .isNotNull()
                .hasSize(2)
                .extracting(NotationResponseDTO::getNote)
                .containsExactly(4.5, 3.0);
    }

    @Test
    @DisplayName("GET /notations/bibliotheque/{id} → bibliothèque inexistante → 404")
    void getNotationsByBibliotheque_notFound_shouldReturn404() {
        // Arrange
        Long bibliothequeId = 999L;
        when(notationService.getNotationsByBibliotheque(bibliothequeId)).thenReturn(null);

        // Act
        ResponseEntity<List<NotationResponseDTO>> response = 
                notationController.getNotationsByBibliotheque(bibliothequeId);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }
}