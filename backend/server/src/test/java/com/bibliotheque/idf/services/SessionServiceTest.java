package com.bibliotheque.idf.services;

import devOps.dtos.SessionDTO;
import devOps.models.SessionEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.SessionRepository;
import devOps.repositories.UtilisateurRepository;
import devOps.services.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UtilisateurRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void creerSession_shouldInitializeFields() {
        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(1L);

        SessionDTO input = new SessionDTO();
        input.setObjectif("Revision maths");
        input.setDureeMinutes(45);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(SessionEntity.class))).thenAnswer(invocation -> {
            SessionEntity s = invocation.getArgument(0);
            s.setId(10L);
            s.setDateCreation(LocalDateTime.now());
            s.setDateDebut(LocalDateTime.now());
            return s;
        });

        SessionDTO dto = sessionService.creerSession(input, 1L);

        assertEquals(10L, dto.getId());
        assertEquals(0, dto.getTempsEcoulesMinutes());
        assertFalse(dto.getCompletee());
    }

    @Test
    void updateTempsEcoule_shouldCompleteSessionWhenDurationReached() {
        SessionEntity session = new SessionEntity();
        session.setId(12L);
        session.setDureeMinutes(30);
        session.setTempsEcoulesMinutes(10);
        session.setCompletee(false);

        when(sessionRepository.findById(12L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(session);

        SessionDTO dto = sessionService.updateTempsEcoule(12L, 30);

        assertTrue(dto.getCompletee());
        assertEquals(30, dto.getTempsEcoulesMinutes());
    }

    @Test
    void getStatistiquesHebdomadaires_shouldComputeWeeklyTotalsAndStreak() {
        SessionEntity j1 = completedSession(30, 1);
        SessionEntity j2 = completedSession(40, 2);
        SessionEntity old = completedSession(20, 8);

        when(sessionRepository.findByUserIdAndCompleteeIsTrueOrderByDateCreationDesc(1L))
                .thenReturn(List.of(j1, j2, old));

        SessionService.SessionStatistiquesDTO stats = sessionService.getStatistiquesHebdomadaires(1L);

        assertEquals(2, stats.getNombreSessions());
        assertEquals(70, stats.getTotalMinutes());
        assertEquals(2, stats.getStreak());
    }

    private SessionEntity completedSession(int duree, int daysAgo) {
        SessionEntity s = new SessionEntity();
        s.setDureeMinutes(duree);
        s.setCompletee(true);
        s.setDateCreation(LocalDateTime.now().minusDays(daysAgo));
        s.setDateFin(LocalDateTime.now().minusDays(daysAgo));
        return s;
    }
}