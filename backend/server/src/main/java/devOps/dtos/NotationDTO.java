package devOps.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotationDTO {

    private Long id;

    @NotNull(message = "La bibliothèque est obligatoire")
    private Long bibliothequeId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimale est 1")
    @Max(value = 5, message = "La note maximale est 5")
    private Integer note;

    private String commentaire;

    @NotNull(message = "La date de visite est obligatoire")
    private LocalDate dateVisite;

    private LocalDate dateNotation;
    private String bibliothequeNom;
}