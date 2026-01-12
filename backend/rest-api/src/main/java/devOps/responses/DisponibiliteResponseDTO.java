package devOps.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibiliteResponseDTO {
    private String lieuId;
    private String livreId;
    private boolean disponible;
}
