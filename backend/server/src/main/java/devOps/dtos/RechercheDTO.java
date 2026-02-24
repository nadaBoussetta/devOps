package devOps.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechercheDTO {

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "L'heure de début est obligatoire")
    private String heureDebut; // Format HH:mm

    @NotBlank(message = "L'heure de fin est obligatoire")
    private String heureFin; // Format HH:mm

    @NotNull(message = "Le rayon est obligatoire")
    @Positive(message = "Le rayon doit être positif")
    private Double rayon; // En kilomètres
}

