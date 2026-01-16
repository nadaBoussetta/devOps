package devOps.component;

import devOps.models.DisponibiliteEntity;
import devOps.repositories.DisponibiliteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DisponibiliteComponent {

    private final DisponibiliteRepository disponibiliteRepository;

    public DisponibiliteComponent(DisponibiliteRepository disponibiliteRepository) {
        this.disponibiliteRepository = disponibiliteRepository;
    }

    public List<DisponibiliteEntity> searchBooks(Long lieuId, String titre) {
        return disponibiliteRepository
                .findByLieuIdAndLivreDispoTrueAndLivreTitreContainingIgnoreCase(lieuId, titre);
    }
}
