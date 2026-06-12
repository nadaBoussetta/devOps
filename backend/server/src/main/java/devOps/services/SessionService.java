package devOps.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devOps.dtos.SessionDTO;
import devOps.models.SessionEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.SessionRepository;
import devOps.repositories.UtilisateurRepository;

@Service
public class         SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UtilisateurRepository userRepository;

    @Transactional
    public SessionDTO creerSession(SessionDTO sessionDTO, Long userId) {
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        SessionEntity session = new SessionEntity();
        session.setUser(user);
        session.setObjectif(sessionDTO.getObjectif());   
        session.setDureeMinutes(sessionDTO.getDureeMinutes());
        session.setTempsEcoulesMinutes(0);
        session.setCompletee(false);

        SessionEntity savedSession = sessionRepository.save(session);
        return convertSessionToDTO(savedSession);
    }

    public SessionDTO getSessionEnCours(Long userId) {
        SessionEntity session = sessionRepository.findByUserIdAndCompleteeIsFalse(userId)
                .orElseThrow(() -> new RuntimeException("Aucune session en cours"));
        updateTempsEcoule(session);
        return convertSessionToDTO(session);
    }

    public List<SessionDTO> getSessionsByUser(Long userId) {
        return sessionRepository.findByUserIdOrderByDateCreationDesc(userId).stream()
                .map(this::convertSessionToDTO)
                .collect(Collectors.toList());
    }

    public List<SessionDTO> getSessionsCompleteesByUser(Long userId) {
        return sessionRepository.findByUserIdAndCompleteeIsTrueOrderByDateCreationDesc(userId).stream()
                .map(this::convertSessionToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SessionDTO updateTempsEcoule(Long sessionId, Integer tempsEcoulesMinutes) {
        SessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));
        session.setTempsEcoulesMinutes(tempsEcoulesMinutes);
        if (tempsEcoulesMinutes >= session.getDureeMinutes()) {
            session.completer();
        }
        return convertSessionToDTO(sessionRepository.save(session));
    }

    @Transactional
    public SessionDTO completerSession(Long sessionId) {
        SessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));
        session.completer();
        return convertSessionToDTO(sessionRepository.save(session));
    }

    @Transactional
    public void supprimerSession(Long sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    public SessionStatistiquesDTO getStatistiquesHebdomadaires(Long userId) {
        List<SessionEntity> sessionsHebdo = sessionRepository
                .findByUserIdAndCompleteeIsTrueOrderByDateCreationDesc(userId).stream()
                .filter(s -> ChronoUnit.DAYS.between(s.getDateCreation(), LocalDateTime.now()) <= 7)
                .collect(Collectors.toList());

        Integer totalMinutes = sessionsHebdo.stream()
                .mapToInt(SessionEntity::getDureeMinutes)
                .sum();

        return new SessionStatistiquesDTO(
                sessionsHebdo.size(),
                totalMinutes,
                calculerStreak(userId)
        );
    }

    private Integer calculerStreak(Long userId) {
        List<SessionEntity> sessions = sessionRepository
                .findByUserIdAndCompleteeIsTrueOrderByDateCreationDesc(userId);
        if (sessions.isEmpty()) return 0;

        int streak = 1;
        LocalDateTime dernierJour = sessions.get(0).getDateFin().toLocalDate().atStartOfDay();

        for (int i = 1; i < sessions.size(); i++) {
            LocalDateTime jourActuel = sessions.get(i).getDateFin().toLocalDate().atStartOfDay();
            if (ChronoUnit.DAYS.between(jourActuel, dernierJour) == 1) {
                streak++;
                dernierJour = jourActuel;
            } else break;
        }
        return streak;
    }

    private void updateTempsEcoule(SessionEntity session) {
        if (!session.getCompletee()) {
            long minutesEcoulees = ChronoUnit.MINUTES.between(session.getDateDebut(), LocalDateTime.now());
            session.setTempsEcoulesMinutes((int) Math.min(minutesEcoulees, session.getDureeMinutes()));
            if (session.getTempsEcoulesMinutes() >= session.getDureeMinutes()) session.completer();
            sessionRepository.save(session);
        }
    }

    private SessionDTO convertSessionToDTO(SessionEntity session) {
        SessionDTO dto = new SessionDTO();
        dto.setId(session.getId());
        dto.setObjectif(session.getObjectif());
        dto.setDureeMinutes(session.getDureeMinutes());
        dto.setTempsEcoulesMinutes(session.getTempsEcoulesMinutes());
        dto.setCompletee(session.getCompletee());
        dto.setDateDebut(session.getDateDebut());
        dto.setDateFin(session.getDateFin());
        dto.setDateCreation(session.getDateCreation());
        dto.setProgressionPourcentage(session.getProgressionPourcentage());
        dto.setTempsRestantMinutes(session.getTempsRestantMinutes());
        return dto;
    }

    public static class SessionStatistiquesDTO {
        public Integer nombreSessions;
        public Integer totalMinutes;
        public Integer streak;

        public SessionStatistiquesDTO(Integer nombreSessions, Integer totalMinutes, Integer streak) {
            this.nombreSessions = nombreSessions;
            this.totalMinutes = totalMinutes;
            this.streak = streak;
        }

        public Integer getNombreSessions() { return nombreSessions; }
        public Integer getTotalMinutes()   { return totalMinutes; }
        public Integer getStreak()         { return streak; }
    }
}