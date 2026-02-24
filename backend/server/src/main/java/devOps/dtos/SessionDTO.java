package devOps.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {

    private Long id;

    @NotBlank(message = "L'objectif est obligatoire")
    private String objectif;

    @Positive(message = "La durée doit être positive")
    private Integer dureeMinutes;

    private Integer tempsEcoulesMinutes;

    private Boolean completee;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private LocalDateTime dateCreation;

    private Double progressionPourcentage;

    private Integer tempsRestantMinutes;
}