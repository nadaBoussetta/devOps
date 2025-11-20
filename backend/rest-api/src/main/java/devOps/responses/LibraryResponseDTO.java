package devOps.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Représente une bibliothèque")
public class LibraryResponseDTO {

    @Schema(description = "ID de la bibliothèque")
    private String id;

    @Schema(description = "Nom de la bibliothèque")
    private String name;

    @Schema(description = "Latitude de la bibliothèque")
    private double latitude;

    @Schema(description = "Longitude de la bibliothèque")
    private double longitude;

    @Schema(description = "Type d'environnement de la bibliothèque")
    private String environment;

    @Schema(description = "Score d'accessibilité de la bibliothèque")
    private double accessibilityScore;

    @Schema(description = "Score météorologique de la bibliothèque")
    private double meteorologyScore;
}
