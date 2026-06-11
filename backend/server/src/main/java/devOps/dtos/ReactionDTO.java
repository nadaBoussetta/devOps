package devOps.dtos;

import java.util.Map;

import devOps.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionDTO {

    private Long postId;
    private ReactionType type;

    // Pour la réponse : comptage par type
    private Map<String, Long> comptages;

    // La réaction de l'utilisateur courant (null si aucune)
    private String maReaction;
}