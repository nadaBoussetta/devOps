package devOps.repositories;

import devOps.models.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    List<SessionEntity> findByUserIdOrderByDateCreationDesc(Long userId);

    List<SessionEntity> findByUserIdAndCompleteeIsTrueOrderByDateCreationDesc(Long userId);

    Optional<SessionEntity> findByUserIdAndCompleteeIsFalse(Long userId);

    Integer countByUserIdAndCompleteeIsTrue(Long userId);
}