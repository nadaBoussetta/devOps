package devOps.repositories;

import devOps.models.DisponibiliteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisponibiliteRepository extends JpaRepository<DisponibiliteEntity, String> {

    List<DisponibiliteEntity> findByLieuIdAndLivreDispoTrueAndLivreTitreContainingIgnoreCase(
            String lieuId,
            String titre
    );
}
