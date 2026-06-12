package devOps.services;

import devOps.dtos.HoraireDTO;
import devOps.dtos.LibraryResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HoraireSearchService {

    private static final Logger log = LoggerFactory.getLogger(HoraireSearchService.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Marque les bibliothèques comme ouvertes ou fermées en fonction des horaires.
     * Une bibliothèque est considérée comme ouverte si son créneau d'ouverture intersecte la plage de recherche.
     */
    public void markOpenStatus(List<LibraryResponseDTO> bibliotheques, String heureDebut, String heureFin) {
        try {
            LocalTime debutRecherche = LocalTime.parse(heureDebut, TIME_FORMATTER);
            LocalTime finRecherche = LocalTime.parse(heureFin, TIME_FORMATTER);
            String jourActuel = getJourAujourdhui();

            log.info("Vérification d'ouverture pour le {} entre {} et {}", jourActuel, debutRecherche, finRecherche);

            for (LibraryResponseDTO biblio : bibliotheques) {
                boolean estOuverte = isOpenDuringForDay(biblio, debutRecherche, finRecherche, jourActuel);
                biblio.setOuvert(estOuverte);
            }
        } catch (Exception e) {
            log.error("Erreur lors du marquage du statut d'ouverture", e);
            bibliotheques.forEach(b -> b.setOuvert(false));
        }
    }

    /**
     * Vérifie si une bibliothèque est ouverte pour un jour spécifique et une plage horaire.
     * Logique d'intersection : (Ouverture < FinRecherche) ET (Fermeture > DebutRecherche)
     */
    private boolean isOpenDuringForDay(LibraryResponseDTO biblio, LocalTime debutRecherche, LocalTime finRecherche, String jour) {
        if (biblio.getHoraires() == null || biblio.getHoraires().isEmpty()) {
            return false;
        }

        for (HoraireDTO horaire : biblio.getHoraires()) {
            if (horaire.getJourSemaine().equalsIgnoreCase(jour)) {
                try {
                    LocalTime ouverture = LocalTime.parse(horaire.getHeureOuverture(), TIME_FORMATTER);
                    LocalTime fermeture = LocalTime.parse(horaire.getHeureFermeture(), TIME_FORMATTER);

                    // Logique d'intersection de créneaux : vrai s'il y a au moins une minute en commun
                    if (ouverture.isBefore(finRecherche) && fermeture.isAfter(debutRecherche)) {
                        return true;
                    }
                } catch (Exception e) {
                    log.warn("Format d'heure invalide pour la bibliothèque '{}'", biblio.getNom());
                }
            }
        }
        return false;
    }

    /**
     * Retourne le jour de la semaine actuel au format LUNDI, MARDI, etc.
     * Utilise explicitement le fuseau horaire Europe/Paris.
     */
    private String getJourAujourdhui() {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Paris"));
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        return switch(dayOfWeek) {
            case MONDAY -> "LUNDI";
            case TUESDAY -> "MARDI";
            case WEDNESDAY -> "MERCREDI";
            case THURSDAY -> "JEUDI";
            case FRIDAY -> "VENDREDI";
            case SATURDAY -> "SAMEDI";
            case SUNDAY -> "DIMANCHE";
        };
    }
}
