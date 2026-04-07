package devOps.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO représentant la réponse complète de l'algorithme d'optimisation d'itinéraire.
 * Contient la séquence ordonnée de bibliothèques couvrant le créneau horaire demandé.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraireResponseDTO {

    /** Adresse de départ fournie par l'utilisateur */
    private String adresseDepart;

    /** Latitude du point de départ */
    private Double latitudeDepart;

    /** Longitude du point de départ */
    private Double longitudeDepart;

    /** Heure de début du créneau global demandé (format HH:mm) */
    private String heureDebutDemandee;

    /** Heure de fin du créneau global demandé (format HH:mm) */
    private String heureFinDemandee;

    /** Liste ordonnée des étapes de l'itinéraire */
    private List<ItineraireEtapeDTO> etapes;

    /** Distance totale de l'itinéraire en km */
    private Double distanceTotale;

    /** Indique si le créneau horaire complet est couvert par l'itinéraire */
    private Boolean creneauCompletementCouvert;

    /** Heure de début réellement couverte par l'itinéraire */
    private String heureDebutCouverte;

    /** Heure de fin réellement couverte par l'itinéraire */
    private String heureFinCouverte;

    /** Message informatif sur l'itinéraire (ex: créneau non couvert, etc.) */
    private String message;
}
