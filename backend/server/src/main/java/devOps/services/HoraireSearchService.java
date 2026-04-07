package devOps.services;

import devOps.dtos.HoraireDTO;
import devOps.dtos.LibraryResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour filtrer les bibliothèques en fonction des horaires d'ouverture.
 */
@Service
public class HoraireSearchService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] JOURS_SEMAINE = {"LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"};

    /**
     * Filtre les bibliothèques en fonction de la plage horaire demandée.
     * 
     * @param bibliotheques liste des bibliothèques à filtrer
     * @param heureDebut heure de début au format HH:mm
     * @param heureFin heure de fin au format HH:mm
     * @return liste des bibliothèques ouvertes pendant la plage horaire
     */
    public List<LibraryResponseDTO> filterByTimeRange(
            List<LibraryResponseDTO> bibliotheques,
            String heureDebut,
            String heureFin) {
        
        try {
            LocalTime debut = LocalTime.parse(heureDebut, TIME_FORMATTER);
            LocalTime fin = LocalTime.parse(heureFin, TIME_FORMATTER);

            return bibliotheques.stream()
                    .filter(biblio -> isOpenDuring(biblio, debut, fin))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Si le parsing échoue, retourner toutes les bibliothèques
            return bibliotheques;
        }
    }

    /**
     * Vérifie si une bibliothèque est ouverte pendant une plage horaire donnée.
     */
    private boolean isOpenDuring(LibraryResponseDTO biblio, LocalTime heureDebut, LocalTime heureFin) {
        if (biblio.getHoraires() == null || biblio.getHoraires().isEmpty()) {
            return false;
        }

        // Récupérer le jour actuel
        String jourAujourdhui = getJourAujourdhui();

        for (HoraireDTO horaire : biblio.getHoraires()) {
            if (horaire.getJourSemaine().equals(jourAujourdhui)) {
                try {
                    LocalTime ouverture = LocalTime.parse(horaire.getHeureOuverture(), TIME_FORMATTER);
                    LocalTime fermeture = LocalTime.parse(horaire.getHeureFermeture(), TIME_FORMATTER);

                    // Vérifier si la plage demandée est couverte par les horaires
                    boolean isOpen = !ouverture.isAfter(heureDebut) && !fermeture.isBefore(heureFin);
                    if (isOpen) {
                        biblio.setOuvert(true);
                        return true;
                    }
                } catch (Exception e) {
                    // Continuer si le parsing échoue
                }
            }
        }

        biblio.setOuvert(false);
        return false;
    }

    /**
     * Marque les bibliothèques comme ouvertes ou fermées en fonction des horaires.
     */
    public void markOpenStatus(List<LibraryResponseDTO> bibliotheques, String heureDebut, String heureFin) {
        try {
            LocalTime debut = LocalTime.parse(heureDebut, TIME_FORMATTER);
            LocalTime fin = LocalTime.parse(heureFin, TIME_FORMATTER);
            String jourAujourdhui = getJourAujourdhui();

            for (LibraryResponseDTO biblio : bibliotheques) {
                biblio.setOuvert(isOpenDuringForDay(biblio, debut, fin, jourAujourdhui));
            }
        } catch (Exception e) {
            // Si le parsing échoue, marquer toutes comme fermées
            bibliotheques.forEach(b -> b.setOuvert(false));
        }
    }

    /**
     * Vérifie si une bibliothèque est ouverte pour un jour spécifique.
     */
    private boolean isOpenDuringForDay(LibraryResponseDTO biblio, LocalTime heureDebut, LocalTime heureFin, String jour) {
        if (biblio.getHoraires() == null || biblio.getHoraires().isEmpty()) {
            return false;
        }

        for (HoraireDTO horaire : biblio.getHoraires()) {
            if (horaire.getJourSemaine().equals(jour)) {
                try {
                    LocalTime ouverture = LocalTime.parse(horaire.getHeureOuverture(), TIME_FORMATTER);
                    LocalTime fermeture = LocalTime.parse(horaire.getHeureFermeture(), TIME_FORMATTER);

                    return !ouverture.isAfter(heureDebut) && !fermeture.isBefore(heureFin);
                } catch (Exception e) {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Retourne le jour de la semaine actuel au format LUNDI, MARDI, etc.
     */
    private String getJourAujourdhui() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        // Calendar.DAY_OF_WEEK: 1 = DIMANCHE, 2 = LUNDI, ..., 7 = SAMEDI
        return JOURS_SEMAINE[(dayOfWeek + 5) % 7];
    }

    /**
     * Retourne le jour de la semaine pour une date donnée.
     */
    public String getJourSemaine(int dayOfWeek) {
        // dayOfWeek: 1 = DIMANCHE, 2 = LUNDI, ..., 7 = SAMEDI
        return JOURS_SEMAINE[(dayOfWeek + 5) % 7];
    }
}
