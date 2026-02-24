package devOps.repositories;

import devOps.models.NotificationEntity;
import devOps.enums.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserIdOrderByDateCreationDesc(Long userId);

    List<NotificationEntity> findByUserIdAndLueIsFalseOrderByDateCreationDesc(Long userId);

    List<NotificationEntity> findByUserIdAndLueIsTrueOrderByDateCreationDesc(Long userId);

    List<NotificationEntity> findByUserIdAndTypeOrderByDateCreationDesc(Long userId, TypeNotification type);

    Integer countByUserIdAndLueIsFalse(Long userId);

    void deleteByUserIdAndLueIsTrue(Long userId);
}
