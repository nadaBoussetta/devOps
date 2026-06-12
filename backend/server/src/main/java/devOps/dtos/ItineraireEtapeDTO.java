package devOps.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant une étape dans l'itinéraire optimisé.
 * Chaque étape correspond à une bibliothèque couvrant un sous-créneau horaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraireEtapeDTO {

    /** Numéro d'ordre de l'étape dans l'itinéraire (commence à 1) */
    private int ordre;

    /** Bibliothèque correspondant à cette étape */
    private LibraryResponseDTO bibliotheque;

    /** Heure de début du créneau couvert par cette bibliothèque (format HH:mm) */
    private String creneauDebut;

    /** Heure de fin du créneau couvert par cette bibliothèque (format HH:mm) */
    private String creneauFin;

    /** Distance depuis l'étape précédente (ou depuis l'adresse de départ pour la 1ère étape), en km */
    private Double distanceDepuisPrecedent;

    /** Distance totale cumulée depuis le point de départ jusqu'à cette étape, en km */
    private Double distanceCumulee;
}
