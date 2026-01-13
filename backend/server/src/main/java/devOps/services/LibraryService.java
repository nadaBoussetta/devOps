package devOps.services;

import devOps.component.LibraryComponent;
import devOps.mappers.LibraryMapper;
import devOps.models.LibraryEntity;
import devOps.dtos.LibraryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryComponent libraryComponent;
    private final LibraryMapper libraryMapper;
    private static final double RAYON_TERRE_KM = 6371.0;

    public LibraryService(LibraryComponent libraryComponent, LibraryMapper libraryMapper) {
        this.libraryComponent = libraryComponent;
        this.libraryMapper = libraryMapper;
    }

    // ===== Recherche principale =====
    public List<LibraryResponseDTO> searchLibraries(
            double userLat,
            double userLon,
            double rayonKm,
            DayOfWeek jour,
            LocalTime heureDebut,
            LocalTime heureFin) {

        return libraryComponent.getAllLibraries()
                .stream()
                // Filtre distance
                .filter(lib -> calculerDistanceKm(userLat, userLon,
                lib.getLatitude(), lib.getLongitude()) <= rayonKm)
                // Filtre horaire
                .filter(lib -> estOuverte(lib.getHeuresOuverture(), jour, heureDebut, heureFin))
                // Map en DTO
                .map(libraryMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ===== Vérifie ouverture pour un jour et une plage horaire =====
    private boolean estOuverte(String heuresOuverture, DayOfWeek jourDemande, LocalTime heureDebut, LocalTime heureFin) {
        if (heuresOuverture == null) {
            return false;
        }

        // Correspondance jours français → DayOfWeek
        String[] joursFrancais = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        String jourStr = joursFrancais[jourDemande.getValue() - 1]; // DayOfWeek 1=Lundi

        String[] lignes = heuresOuverture.split("\n");
        for (String ligne : lignes) {
            if (!ligne.startsWith(jourStr)) {
                continue;
            }

            String plagesStr = ligne.substring(ligne.indexOf(":") + 1).trim();
            String[] blocs = plagesStr.split("et"); // plusieurs plages possibles

            for (String bloc : blocs) {
                bloc = bloc.trim().replace("h", ""); // "14h-18h" → "14-18"
                String[] heures = bloc.split("-");
                if (heures.length != 2) {
                    continue;
                }

                try {
                    LocalTime debut = LocalTime.parse(heures[0].trim() + ":00");
                    LocalTime fin = LocalTime.parse(heures[1].trim() + ":00");

                    if (!heureDebut.isBefore(debut) && !heureFin.isAfter(fin)) {
                        return true;
                    }

                } catch (Exception e) {
                    // Ignore les formats incorrects
                }
            }
        }
        return false;
    }

    // ===== Distance Haversine =====
    private double calculerDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAYON_TERRE_KM * c;
    }
}
