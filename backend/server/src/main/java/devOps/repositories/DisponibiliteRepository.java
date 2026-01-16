package devOps.repositories;

import devOps.models.DisponibiliteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisponibiliteRepository extends JpaRepository<DisponibiliteEntity, Long> {

    List<DisponibiliteEntity> findByLieuIdAndLivreDispoTrueAndLivreTitreContainingIgnoreCase(
            Long lieuId,
            String titre
    );
}
