package devOps.repositories;

import devOps.models.CachedLibraryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachedLibraryRepository extends JpaRepository<CachedLibraryEntity, Long> {
}
