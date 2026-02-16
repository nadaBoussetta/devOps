package devOps.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Livre disponible dans un lieu")
public class LivreResponseDTO {

    @Schema(description = "ID du livre")
    private String id;

    @Schema(description = "Titre du livre")
    private String titre;

    @Schema(description = "Auteur du livre")
    private String auteur;

    @Schema(description = "ISBN")
    private String isbn;
}