package devOps.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Body reçu par POST /api/livres/disponibilite.
 * Contient le titre cherché + la liste des bibliothèques IDF
 * retournées par /api/bibliotheques/recherche.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibiliteRequestDTO {

    private String titre;

    /** Liste de bibliothèques IDF à interroger */
    private List<BibliothequeSimpleDTO> bibliotheques;

    /**
     * Représentation minimale d'une bibliothèque IDF
     * (sous-ensemble de LibraryResponseDTO).
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BibliothequeSimpleDTO {
        private String nom;
        private String adresse;
        private Double distance;
        private Boolean ouvert;
    }
}