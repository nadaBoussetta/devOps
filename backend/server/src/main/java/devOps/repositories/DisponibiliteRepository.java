package devOps.repositories;

import devOps.models.DisponibiliteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DisponibiliteRepository extends JpaRepository<DisponibiliteEntity, String> {

    List<DisponibiliteEntity> findByLieuIdAndLivreDispoTrueAndLivreTitreContainingIgnoreCase(
            String lieuId,
            String titre
    );

    Optional<DisponibiliteEntity> findByLieuIdAndLivreId(String lieuId, String livreId);

    boolean existsByLieuIdAndLivreIdAndLivreDispoTrue(String lieuId, String livreId);
}
