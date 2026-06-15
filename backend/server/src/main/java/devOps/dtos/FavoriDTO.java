package devOps.dtos;

import java.time.LocalDateTime;

import devOps.enums.TypeBibliotheque;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriDTO {

    private Long id;
    private Long bibliothequeId;
    private String bibliothequeNom;
    private LocalDateTime dateAjout;

    // Champs supplémentaires pour les bibliothèques externes (API IDF)
    // Optionnels : remplis uniquement lors de l'ajout depuis la recherche
    private String bibliothequeAdresse;
    private Double bibliothequeLatitude;
    private Double bibliothequeLongitude;
    private TypeBibliotheque bibliothequeType;
}