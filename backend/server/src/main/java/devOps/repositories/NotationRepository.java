package devOps.repositories;

import devOps.models.NotationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotationRepository extends JpaRepository<NotationEntity, Long> {

    List<NotationEntity> findByUserIdOrderByDateNotationDesc(Long userId);

    List<NotationEntity> findByBibliothequeIdOrderByDateNotationDesc(Long bibliothequeId);

    Optional<NotationEntity> findByUserIdAndBibliothequeId(Long userId, Long bibliothequeId);

    Boolean existsByUserIdAndBibliothequeId(Long userId, Long bibliothequeId);
}