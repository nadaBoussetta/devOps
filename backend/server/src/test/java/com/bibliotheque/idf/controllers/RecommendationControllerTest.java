package com.bibliotheque.idf.controllers;  // adapte si ton package est devOps.controllers

import com.bibliotheque.idf.dtos.RecommendationDTO; // adapte le nom du DTO si différent
import com.bibliotheque.idf.services.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du RecommendationController")
class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    private List<RecommendationDTO> dummyRecommendations;

    @BeforeEach
    void setUp() {
        // Données de test communes
        RecommendationDTO rec1 = new RecommendationDTO();
        rec1.setId(1L);
        rec1.setTitre("Java pour les nuls");
        rec1.setScore(0.95);

        RecommendationDTO rec2 = new RecommendationDTO();
        rec2.setId(2L);
        rec2.setTitre("Clean Code");
        rec2.setScore(0.88);

        dummyRecommendations = List.of(rec1, rec2);
    }

    @Test
    @DisplayName("GET /recommendations/user/{userId} → utilisateur avec recommandations → 200 + liste non vide")
    void getRecommendationsForUser_hasRecommendations_shouldReturn200AndList() {
        // Arrange
        Long userId = 42L;
        when(recommendationService.getRecommendationsForUser(userId))
                .thenReturn(dummyRecommendations);

        // Act
        ResponseEntity<List<RecommendationDTO>> response =
                recommendationController.getRecommendationsForUser(userId);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody())
                .isNotNull()
                .hasSize(2)
                .extracting(RecommendationDTO::getTitre)
                .containsExactly("Java pour les nuls", "Clean Code");
    }

    @Test
    @DisplayName("GET /recommendations/user/{userId} → pas de recommandation → 200 + liste vide")
    void getRecommendationsForUser_noRecommendations_shouldReturn200EmptyList() {
        // Arrange
        Long userId = 99L;
        when(recommendationService.getRecommendationsForUser(userId))
                .thenReturn(List.of());

        // Act
        ResponseEntity<List<RecommendationDTO>> response =
                recommendationController.getRecommendationsForUser(userId);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("GET /recommendations/user/{userId} → utilisateur inconnu → 404 ou 200 vide")
    void getRecommendationsForUser_userNotFound_shouldReturnNotFoundOrEmpty() {
        // Arrange
        Long userId = 999L;
        when(recommendationService.getRecommendationsForUser(userId))
                .thenReturn(null);  // ou throw exception selon ton impl

        // Act
        ResponseEntity<List<RecommendationDTO>> response =
                recommendationController.getRecommendationsForUser(userId);

        // Assert
        assertThat(response.getStatusCodeValue()).isIn(200, 404);
        assertThat(response.getBody()).isNotNull().isEmpty();
    }
}