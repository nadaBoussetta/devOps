package devOps.repositories;

import devOps.models.FavoriEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriRepository extends JpaRepository<FavoriEntity, Long> {

    List<FavoriEntity> findByUser_IdOrderByDateAjoutDesc(Long userId);

    Optional<FavoriEntity> findByUser_IdAndLibraryEntity_Id(Long userId, Long bibliothequeId);

    Boolean existsByUser_IdAndLibraryEntity_Id(Long userId, Long bibliothequeId);

    void deleteByUser_IdAndLibraryEntity_Id(Long userId, Long bibliothequeId);
}