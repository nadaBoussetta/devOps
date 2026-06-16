package devOps.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Résultat retourné par POST /api/livres/disponibilite
 * pour une bibliothèque donnée.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibiliteResultatDTO {

    /** Nom de la bibliothèque IDF */
    private String nomBibliotheque;

    /** Adresse de la bibliothèque */
    private String adresse;

    /** Distance en km depuis l'adresse de recherche */
    private Double distance;

    /** La bibliothèque est-elle ouverte sur le créneau demandé ? */
    private Boolean ouvert;

    /** Le livre a été trouvé dans cette bibliothèque */
    private Boolean livreDisponible;

    /** Les exemplaires trouvés (peut être vide si non disponible) */
    private List<LivreResponseDTO> exemplaires;
}