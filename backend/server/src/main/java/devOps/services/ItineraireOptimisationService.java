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
 *
 * <p>L'algorithme fonctionne de manière greedy (gloutonne) :
 * à chaque étape, il sélectionne parmi les bibliothèques candidates celle qui :
 * <ol>
 *   <li>Ouvre au plus tôt à l'heure courante (ou avant)</li>
 *   <li>Est la plus proche du point courant (adresse de départ ou dernière bibliothèque)</li>
 *   <li>Couvre le maximum de temps restant</li>
 * </ol>
 * Le processus se répète jusqu'à couvrir l'intégralité du créneau demandé
 * ou jusqu'à épuisement des bibliothèques disponibles.
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

    /**
     * Calcule l'itinéraire optimisé pour couvrir le créneau horaire demandé.
     *
     * @param recherche DTO contenant l'adresse, les heures et le rayon de recherche
     * @return l'itinéraire optimisé avec la séquence de bibliothèques
     */
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
            LibraryResponseDTO dto = convertIleDeFranceToDTO(idfLib, latDepart, lonDepart);
            candidats.add(dto);
        }

        // 4. Déterminer le jour de la semaine actuel
        String jourActuel = getJourActuel();

        // 5. Parser les heures demandées
        LocalTime heureDebutDemandee;
        LocalTime heureFinDemandee;
        try {
            heureDebutDemandee = LocalTime.parse(recherche.getHeureDebut(), TIME_FORMATTER);
            heureFinDemandee = LocalTime.parse(recherche.getHeureFin(), TIME_FORMATTER);
        } catch (Exception e) {
            response.setEtapes(new ArrayList<>());
            response.setDistanceTotale(0.0);
            response.setCreneauCompletementCouvert(false);
            response.setMessage("Format d'heure invalide. Utilisez le format HH:mm.");
            return response;
        }

        // 6. Lancer l'algorithme d'optimisation
        List<ItineraireEtapeDTO> etapes = new ArrayList<>();
        LocalTime heureCourante = heureDebutDemandee;
        double latCourante = latDepart;
        double lonCourante = lonDepart;
        double distanceCumulee = 0.0;
        List<Long> bibliothequesDejaSelectionnees = new ArrayList<>();

        while (heureCourante.isBefore(heureFinDemandee)) {
            // Trouver la meilleure bibliothèque pour le créneau restant
            MeilleurCandidat meilleur = trouverMeilleurCandidat(
                    candidats, jourActuel, heureCourante, heureFinDemandee,
                    latCourante, lonCourante, bibliothequesDejaSelectionnees);

            if (meilleur == null) {
                // Aucune bibliothèque disponible pour couvrir le reste du créneau
                break;
            }

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

            // Mettre à jour la position courante et l'heure couverte
            latCourante = meilleur.bibliotheque.getLatitude();
            lonCourante = meilleur.bibliotheque.getLongitude();
            heureCourante = meilleur.heureFermeture;
            bibliothequesDejaSelectionnees.add(meilleur.bibliotheque.getId());
        }

        // 7. Construire la réponse
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

            if (couvertCompletement) {
                response.setMessage("Itinéraire optimal trouvé ! Le créneau " +
                        recherche.getHeureDebut() + " - " + recherche.getHeureFin() +
                        " est entièrement couvert en " + etapes.size() + " étape(s).");
            } else {
                response.setMessage("Itinéraire partiel : le créneau est couvert de " +
                        heureDebutCouverte + " à " + heureFinCouverte +
                        ". Aucune bibliothèque disponible pour couvrir la fin du créneau jusqu'à " +
                        recherche.getHeureFin() + ". Essayez d'augmenter le rayon de recherche.");
            }
        }

        return response;
    }

    /**
     * Trouve le meilleur candidat parmi les bibliothèques disponibles pour couvrir
     * le prochain sous-créneau horaire à partir de l'heure courante.
     *
     * <p>Stratégie de sélection :
     * <ol>
     *   <li>La bibliothèque doit être ouverte à l'heure courante (ouverture <= heureCourante)</li>
     *   <li>Parmi les candidates valides, on privilégie celle qui ferme le plus tard
     *       (maximise la couverture horaire)</li>
     *   <li>En cas d'égalité sur la fermeture, on choisit la plus proche</li>
     * </ol>
     */
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
            // Ignorer les bibliothèques déjà sélectionnées
            if (dejaSelectionnees.contains(biblio.getId())) {
                continue;
            }

            if (biblio.getHoraires() == null || biblio.getHoraires().isEmpty()) {
                continue;
            }

            // Chercher l'horaire du jour concerné
            for (HoraireDTO horaire : biblio.getHoraires()) {
                if (!jour.equalsIgnoreCase(horaire.getJourSemaine())) {
                    continue;
                }

                LocalTime ouverture;
                LocalTime fermeture;
                try {
                    ouverture = LocalTime.parse(horaire.getHeureOuverture(), TIME_FORMATTER);
                    fermeture = LocalTime.parse(horaire.getHeureFermeture(), TIME_FORMATTER);
                } catch (Exception e) {
                    continue;
                }

                // La bibliothèque doit être ouverte à l'heure courante
                // (ouverture <= heureCourante ET fermeture > heureCourante)
                if (ouverture.isAfter(heureCourante)) {
                    // La bibliothèque n'est pas encore ouverte à l'heure courante
                    continue;
                }
                if (!fermeture.isAfter(heureCourante)) {
                    // La bibliothèque est déjà fermée
                    continue;
                }

                // Calculer la distance depuis le point courant
                double distance = DistanceCalculator.calculateDistance(
                        latCourante, lonCourante,
                        biblio.getLatitude(), biblio.getLongitude());

                MeilleurCandidat candidat = new MeilleurCandidat(biblio, fermeture, distance);

                // Sélectionner le meilleur candidat :
                // 1. Priorité à celui qui ferme le plus tard (couvre le plus de temps)
                // 2. En cas d'égalité, priorité au plus proche
                if (meilleur == null) {
                    meilleur = candidat;
                } else {
                    int comparaisonFermeture = fermeture.compareTo(meilleur.heureFermeture);
                    if (comparaisonFermeture > 0) {
                        // Ferme plus tard → meilleure couverture
                        meilleur = candidat;
                    } else if (comparaisonFermeture == 0 && distance < meilleur.distance) {
                        // Même fermeture → choisir le plus proche
                        meilleur = candidat;
                    }
                }
            }
        }

        return meilleur;
    }

    /**
     * Retourne le jour de la semaine actuel au format LUNDI, MARDI, etc.
     */
    private String getJourActuel() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        // Calendar.DAY_OF_WEEK: 1 = DIMANCHE, 2 = LUNDI, ..., 7 = SAMEDI
        return JOURS_SEMAINE[(dayOfWeek + 5) % 7];
    }

    /**
     * Convertit un DTO IleDeFrance en LibraryResponseDTO avec horaires parsés.
     */
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
                latRef, lonRef,
                idfLib.getGeo().getLat(), idfLib.getGeo().getLon());
        dto.setDistance(Math.round(distance * 100.0) / 100.0);

        dto.setHoraires(horaireParser.parseHoraires(idfLib.getHeuresOuverture()));
        dto.setOuvert(false);

        return dto;
    }

    /**
     * Classe interne représentant un candidat optimal lors de la sélection.
     */
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
