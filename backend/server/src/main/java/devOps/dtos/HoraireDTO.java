package devOps.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoraireDTO {

    private Long id;
    private String jourSemaine;
    private String heureOuverture;
    private String heureFermeture;
}
