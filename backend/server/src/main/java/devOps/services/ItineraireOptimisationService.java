package devOps.services;

import devOps.dtos.HoraireDTO;
import devOps.dtos.IleDeFranceLibraryDTO;
import devOps.dtos.ItineraireEtapeDTO;
import devOps.dtos.ItineraireResponseDTO;
import devOps.dtos.LibraryResponseDTO;
import devOps.dtos.RechercheDTO;
import devOps.util.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Service implémentant l'algorithme d'optimisation d'itinéraire entre bibliothèques.
 */
@Service
public class ItineraireOptimisationService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] JOURS_SEMAINE = {"LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"};

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private IleDeFranceLibraryApiService ileDeFranceLibraryApiService;

    @Autowired
    private HoraireParser horaireParser;

    public ItineraireResponseDTO calculerItineraire(RechercheDTO recherche) {

        // 1. Géocoder l'adresse de départ
        double[] coordonnees = geolocationService.geocodeAdresse(recherche.getAdresse());

        ItineraireResponseDTO response = new ItineraireResponseDTO();
        response.setAdresseDepart(recherche.getAdresse());
        response.setHeureDebutDemandee(recherche.getHeureDebut());
        response.setHeureFinDemandee(recherche.getHeureFin());

        if (coordonnees == null) {
            response.setEtapes(new ArrayList<>());
            response.setDistanceTotale(0.0);
            response.setCreneauCompletementCouvert(false);
            response.setMessage("Adresse introuvable. Veuillez vérifier l'adresse saisie.");
            return response;
        }

        double latDepart = coordonnees[0];
        double lonDepart = coordonnees[1];
        response.setLatitudeDepart(latDepart);
        response.setLongitudeDepart(lonDepart);

        // 2. Récupérer toutes les bibliothèques dans le rayon
        List<IleDeFranceLibraryDTO> idfLibraries = ileDeFranceLibraryApiService.searchLibraries(
                latDepart, lonDepart, recherche.getRayon());

        // 3. Convertir en LibraryResponseDTO avec horaires parsés
        List<LibraryResponseDTO> candidats = new ArrayList<>();
        for (IleDeFranceLibraryDTO idfLib : idfLibraries) {
            candidats.add(convertIleDeFranceToDTO(idfLib, latDepart, lonDepart));
        }

        // 4. Jour actuel
        String jourActuel = getJourActuel();

        // 5. Parser les heures demandées
        LocalTime heureDebutDemandee = LocalTime.parse(recherche.getHeureDebut(), TIME_FORMATTER);
        LocalTime heureFinDemandee = LocalTime.parse(recherche.getHeureFin(), TIME_FORMATTER);

        // 6. Initialisation de l'itinéraire
        List<ItineraireEtapeDTO> etapes = new ArrayList<>();
        LocalTime heureCourante = heureDebutDemandee;
        double latCourante = latDepart;
        double lonCourante = lonDepart;
        double distanceCumulee = 0.0;
        List<Long> bibliothequesDejaSelectionnees = new ArrayList<>();

        boolean debutCouvert = false;

        while (heureCourante.isBefore(heureFinDemandee)) {

            MeilleurCandidat meilleur = trouverMeilleurCandidat(
                    candidats, jourActuel, heureCourante, heureFinDemandee,
                    latCourante, lonCourante, bibliothequesDejaSelectionnees);

            if (meilleur == null) {
                // Aucune bibliothèque ouverte à l'heureCourante
                LocalTime prochainDebut = trouverProchainCreneauDisponible(candidats, jourActuel, heureCourante);
                if (prochainDebut == null) break; // pas de bibliothèques restantes
                if (!debutCouvert) {
                    // Marquer le début réel couvert après attente
                    heureCourante = prochainDebut;
                } else {
                    break; // fin partielle
                }
                continue;
            }

            debutCouvert = true;

            // Calculer la distance depuis le point courant
            double distanceEtape = DistanceCalculator.calculateDistance(
                    latCourante, lonCourante,
                    meilleur.bibliotheque.getLatitude(), meilleur.bibliotheque.getLongitude());
            distanceEtape = Math.round(distanceEtape * 100.0) / 100.0;
            distanceCumulee += distanceEtape;
            distanceCumulee = Math.round(distanceCumulee * 100.0) / 100.0;

            // Créer l'étape
            ItineraireEtapeDTO etape = new ItineraireEtapeDTO();
            etape.setOrdre(etapes.size() + 1);
            etape.setBibliotheque(meilleur.bibliotheque);
            etape.setCreneauDebut(heureCourante.format(TIME_FORMATTER));
            etape.setCreneauFin(meilleur.heureFermeture.format(TIME_FORMATTER));
            etape.setDistanceDepuisPrecedent(distanceEtape);
            etape.setDistanceCumulee(distanceCumulee);

            etapes.add(etape);

            // Mise à jour
            latCourante = meilleur.bibliotheque.getLatitude();
            lonCourante = meilleur.bibliotheque.getLongitude();
            heureCourante = meilleur.heureFermeture;
            bibliothequesDejaSelectionnees.add(meilleur.bibliotheque.getId());
        }

        response.setEtapes(etapes);
        response.setDistanceTotale(distanceCumulee);

        if (etapes.isEmpty()) {
            response.setCreneauCompletementCouvert(false);
            response.setMessage("Aucune bibliothèque disponible dans ce rayon pour ce créneau horaire. "
                    + "Essayez d'augmenter le rayon de recherche.");
            response.setHeureDebutCouverte(null);
            response.setHeureFinCouverte(null);
        } else {
            String heureDebutCouverte = etapes.get(0).getCreneauDebut();
            String heureFinCouverte = etapes.get(etapes.size() - 1).getCreneauFin();
            response.setHeureDebutCouverte(heureDebutCouverte);
            response.setHeureFinCouverte(heureFinCouverte);

            LocalTime finCouverte = LocalTime.parse(heureFinCouverte, TIME_FORMATTER);
            boolean couvertCompletement = !finCouverte.isBefore(heureFinDemandee);
            response.setCreneauCompletementCouvert(couvertCompletement);

            if (couvertCompletement && heureDebutCouverte.equals(recherche.getHeureDebut())) {
                response.setMessage("Itinéraire optimal trouvé ! Le créneau "
                        + recherche.getHeureDebut() + " - " + recherche.getHeureFin() +
                        " est entièrement couvert en " + etapes.size() + " étape(s).");
            } else {
                response.setMessage("Itinéraire partiel : le créneau est couvert de " +
                        heureDebutCouverte + " à " + heureFinCouverte +
                        ". Aucune bibliothèque disponible pour couvrir la totalité du créneau demandé. "
                        + "Essayez d'augmenter le rayon de recherche.");
            }
        }

        return response;
    }

    // --- Méthodes auxiliaires ---

    private MeilleurCandidat trouverMeilleurCandidat(
            List<LibraryResponseDTO> candidats,
            String jour,
            LocalTime heureCourante,
            LocalTime heureFinDemandee,
            double latCourante,
            double lonCourante,
            List<Long> dejaSelectionnees) {

        MeilleurCandidat meilleur = null;

        for (LibraryResponseDTO biblio : candidats) {
            if (dejaSelectionnees.contains(biblio.getId()) || biblio.getHoraires() == null) continue;

            for (HoraireDTO horaire : biblio.getHoraires()) {
                if (!jour.equalsIgnoreCase(horaire.getJourSemaine())) continue;

                LocalTime ouverture = LocalTime.parse(horaire.getHeureOuverture(), TIME_FORMATTER);
                LocalTime fermeture = LocalTime.parse(horaire.getHeureFermeture(), TIME_FORMATTER);

                if (fermeture.isBefore(heureCourante) || ouverture.isAfter(heureFinDemandee)) continue;

                if (ouverture.isAfter(heureCourante)) continue;

                double distance = DistanceCalculator.calculateDistance(
                        latCourante, lonCourante, biblio.getLatitude(), biblio.getLongitude());

                MeilleurCandidat candidat = new MeilleurCandidat(biblio, fermeture, distance);

                if (meilleur == null || fermeture.isAfter(meilleur.heureFermeture)
                        || (fermeture.equals(meilleur.heureFermeture) && distance < meilleur.distance)) {
                    meilleur = candidat;
                }
            }
        }
        return meilleur;
    }

    private LocalTime trouverProchainCreneauDisponible(List<LibraryResponseDTO> candidats, String jour, LocalTime heureCourante) {
        LocalTime prochain = null;
        for (LibraryResponseDTO biblio : candidats) {
            if (biblio.getHoraires() == null) continue;
            for (HoraireDTO horaire : biblio.getHoraires()) {
                if (!jour.equalsIgnoreCase(horaire.getJourSemaine())) continue;
                LocalTime ouverture = LocalTime.parse(horaire.getHeureOuverture(), TIME_FORMATTER);
                if (ouverture.isAfter(heureCourante)) {
                    if (prochain == null || ouverture.isBefore(prochain)) prochain = ouverture;
                }
            }
        }
        return prochain;
    }

    private String getJourActuel() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return JOURS_SEMAINE[(dayOfWeek + 5) % 7];
    }

    private LibraryResponseDTO convertIleDeFranceToDTO(IleDeFranceLibraryDTO idfLib, double latRef, double lonRef) {
        LibraryResponseDTO dto = new LibraryResponseDTO();
        dto.setId(Math.abs((long) idfLib.getNomEtablissement().hashCode() * 31
                + (long) idfLib.getNomRue().hashCode()));
        dto.setNom(idfLib.getNomEtablissement());
        dto.setAdresse(idfLib.getNomRue() + ", " + idfLib.getCodePostal() + " " + idfLib.getCommune());
        dto.setLatitude(idfLib.getGeo().getLat());
        dto.setLongitude(idfLib.getGeo().getLon());
        dto.setNoteGlobale(0.0);
        dto.setNombreNotations(0);
        dto.setSearchLatitude(latRef);
        dto.setSearchLongitude(lonRef);
        double distance = DistanceCalculator.calculateDistance(
                latRef, lonRef, idfLib.getGeo().getLat(), idfLib.getGeo().getLon());
        dto.setDistance(Math.round(distance * 100.0) / 100.0);
        dto.setHoraires(horaireParser.parseHoraires(idfLib.getHeuresOuverture()));
        dto.setOuvert(false);
        return dto;
    }

    private static class MeilleurCandidat {
        final LibraryResponseDTO bibliotheque;
        final LocalTime heureFermeture;
        final double distance;
        MeilleurCandidat(LibraryResponseDTO bibliotheque, LocalTime heureFermeture, double distance) {
            this.bibliotheque = bibliotheque;
            this.heureFermeture = heureFermeture;
            this.distance = distance;
        }
    }
}