package devOps.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;

@Getter
@Setter
@Data
public class PublicationRequestDTO {
    private String message;
    private Long utilisateurId;
    private Long repondeurId; // Optionnel
}
