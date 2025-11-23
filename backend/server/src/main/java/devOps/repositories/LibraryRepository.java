package devOps.repositories;

import devOps.models.LibraryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface LibraryRepository extends JpaRepository<LibraryEntity, Long> {
    @Query("SELECT l FROM LibraryEntity l")
    List<LibraryEntity> findAllLibraries();
}
