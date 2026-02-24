package devOps.dtos;

import devOps.enums.TypeNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;

    private TypeNotification type;

    private String titre;

    private String message;

    private Long bibliothequeId;

    private String bibliothequeNom;

    private Boolean lue;

    private LocalDateTime dateCreation;

    private LocalDateTime dateConsultation;
}