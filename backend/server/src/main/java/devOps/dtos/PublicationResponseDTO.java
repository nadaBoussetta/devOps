package devOps.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import lombok.Data;
@Getter
@Setter
@Data
public class PublicationResponseDTO {
    private Long id;
    private String message;
    private Date date;
    private Long utilisateurId;
    private Long repondeurId;
}
