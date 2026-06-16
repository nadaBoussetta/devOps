package devOps.services;

import devOps.dtos.*;
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

        List<IleDeFranceLibraryDTO> idfLibraries = ileDeFranceLibraryApiService.searchLibraries(
                latDepart, lonDepart, recherche.getRayon());

        List<LibraryResponseDTO> candidats = new ArrayList<>();
        for (IleDeFranceLibraryDTO idfLib : idfLibraries) {
            candidats.add(convertIleDeFranceToDTO(idfLib, latDepart, lonDepart));
        }

        String jourActuel = getJourActuel();
        LocalTime heureDebutDemandee = LocalTime.parse(recherche.getHeureDebut(), TIME_FORMATTER);
        LocalTime heureFinDemandee = LocalTime.parse(recherche.getHeureFin(), TIME_FORMATTER);

        // Algorithme amélioré : Recherche de l'itinéraire avec le moins d'étapes et la distance minimale
        // On utilise une approche de programmation dynamique simplifiée (recherche du chemin le plus court dans un DAG)
        List<ItineraireEtapeDTO> meilleuresEtapes = resoudreItineraire(
                candidats, jourActuel, heureDebutDemandee, heureFinDemandee, latDepart, lonDepart);

        response.setEtapes(meilleuresEtapes);
        double distanceTotale = meilleuresEtapes.isEmpty() ? 0.0 : meilleuresEtapes.get(meilleuresEtapes.size() - 1).getDistanceCumulee();
        response.setDistanceTotale(distanceTotale);

        if (meilleuresEtapes.isEmpty()) {
            response.setCreneauCompletementCouvert(false);
            response.setMessage("Aucune bibliothèque disponible pour ce créneau.");
        } else {
            String hDebut = meilleuresEtapes.get(0).getCreneauDebut();
            String hFin = meilleuresEtapes.get(meilleuresEtapes.size() - 1).getCreneauFin();
            response.setHeureDebutCouverte(hDebut);
            response.setHeureFinCouverte(hFin);

            boolean complet = hDebut.equals(recherche.getHeureDebut()) &&
                    !LocalTime.parse(hFin, TIME_FORMATTER).isBefore(heureFinDemandee);
            response.setCreneauCompletementCouvert(complet);
            response.setMessage(complet ? "Itinéraire optimal trouvé !" : "Itinéraire partiel trouvé.");
        }

        return response;
    }

    private List<ItineraireEtapeDTO> resoudreItineraire(
            List<LibraryResponseDTO> candidats, String jour,
            LocalTime debut, LocalTime fin, double latDep, double lonDep) {

        // Structure pour stocker le meilleur chemin vers chaque bibliothèque à un certain moment
        // Pour rester pragmatique, on garde l'approche gloutonne mais améliorée pour regarder un coup d'avance
        // ou privilégier la couverture maximale avec le moins de sauts.

        List<ItineraireEtapeDTO> etapes = new ArrayList<>();
        LocalTime heureCourante = debut;
        double latC = latDep;
        double lonC = lonDep;
        double distC = 0.0;
        List<Long> selectionnees = new ArrayList<>();

        while (heureCourante.isBefore(fin)) {
            LibraryResponseDTO meilleur = null;
            LocalTime meilleureFin = heureCourante;
            double meilleureDist = Double.MAX_VALUE;

            for (LibraryResponseDTO c : candidats) {
                if (selectionnees.contains(c.getId())) continue;

                for (HoraireDTO h : c.getHoraires()) {
                    if (!jour.equalsIgnoreCase(h.getJourSemaine())) continue;

                    LocalTime o = LocalTime.parse(h.getHeureOuverture(), TIME_FORMATTER);
                    LocalTime f = LocalTime.parse(h.getHeureFermeture(), TIME_FORMATTER);

                    if (o.isAfter(heureCourante) || f.isBefore(heureCourante) || !f.isAfter(heureCourante)) continue;

                    double d = DistanceCalculator.calculateDistance(latC, lonC, c.getLatitude(), c.getLongitude());
                    LocalTime fEffective = f.isAfter(fin) ? fin : f;

                    // Score : on veut maximiser la durée de l'étape et minimiser la distance
                    // Un bon compromis : privilégier la fin la plus tardive, puis la distance
                    if (meilleur == null || fEffective.isAfter(meilleureFin) || (fEffective.equals(meilleureFin) && d < meilleureDist)) {
                        meilleur = c;
                        meilleureFin = fEffective;
                        meilleureDist = d;
                    }
                }
            }

            if (meilleur == null) {
                // Essayer de trouver une bibliothèque qui ouvre plus tard (attente)
                LocalTime prochain = trouverProchainCreneauDisponible(candidats, jour, heureCourante);
                if (prochain == null || !prochain.isBefore(fin)) break;
                heureCourante = prochain;
                continue;
            }

            ItineraireEtapeDTO etape = new ItineraireEtapeDTO();
            etape.setOrdre(etapes.size() + 1);
            etape.setBibliotheque(meilleur);
            etape.setCreneauDebut(heureCourante.format(TIME_FORMATTER));
            etape.setCreneauFin(meilleureFin.format(TIME_FORMATTER));
            etape.setDistanceDepuisPrecedent(Math.round(meilleureDist * 100.0) / 100.0);
            distC += meilleureDist;
            etape.setDistanceCumulee(Math.round(distC * 100.0) / 100.0);

            etapes.add(etape);
            selectionnees.add(meilleur.getId());
            latC = meilleur.getLatitude();
            lonC = meilleur.getLongitude();
            heureCourante = meilleureFin;
        }

        return etapes;
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

                // Si le créneau finit avant l'heure courante ou commence après la fin demandée, on ignore
                if (fermeture.isBefore(heureCourante) || !fermeture.isAfter(heureCourante) || ouverture.isAfter(heureFinDemandee)) continue;

                // Si la bibliothèque n'est pas encore ouverte à l'heure courante, on ignore (on gère l'attente ailleurs)
                if (ouverture.isAfter(heureCourante)) continue;

                double distance = DistanceCalculator.calculateDistance(
                        latCourante, lonCourante, biblio.getLatitude(), biblio.getLongitude());

                // Stratégie : on prend celle qui ferme le plus tard pour couvrir le plus de temps possible
                // En cas d'égalité, on prend la plus proche
                if (meilleur == null || fermeture.isAfter(meilleur.heureFermeture)
                        || (fermeture.equals(meilleur.heureFermeture) && distance < meilleur.distance)) {
                    meilleur = new MeilleurCandidat(biblio, fermeture, distance);
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
