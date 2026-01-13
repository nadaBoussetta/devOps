package devOps.component;

import devOps.exceptions.technicals.NotFoundLibraryEntityException;
import devOps.models.LibraryEntity;
import devOps.repositories.LibraryRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class LibraryComponent {

    private final LibraryRepository libraryRepository;

    public LibraryComponent(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    public LibraryEntity getLibrary(String id) {
        return libraryRepository.findById(id)
            .orElseThrow(() -> new NotFoundLibraryEntityException(
                "Bibliothèque introuvable : " + id));
    }

    public List<LibraryEntity> getAllLibraries() {
        return libraryRepository.findAll();
    }

    public LibraryEntity saveLibrary(LibraryEntity library) {
        return libraryRepository.save(library);
    }
}
