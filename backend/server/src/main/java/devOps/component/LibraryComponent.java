package devOps.component;

import devOps.exceptions.technicals.NotFoundLibraryEntityException;
import devOps.models.LibraryEntity;
import devOps.repositories.LibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LibraryComponent {

    private final LibraryRepository libraryRepository;

    public LibraryEntity getLibrary(String id) throws NotFoundLibraryEntityException {
        return libraryRepository.findById(id)
                .orElseThrow(() -> new NotFoundLibraryEntityException(
                        String.format("La bibliothèque %s n'a pas été trouvée", id)));
    }

    public List<LibraryEntity> getAllLibraries() {
        return libraryRepository.findAll();
    }

    public LibraryEntity getBestLibrary(double userLat, double userLon) throws NotFoundLibraryEntityException {
        return libraryRepository.findAll()
                .stream()
                .min((lib1, lib2) -> Double.compare(
                        distance(userLat, userLon, lib1.getLatitude(), lib1.getLongitude()),
                        distance(userLat, userLon, lib2.getLatitude(), lib2.getLongitude())))
                .orElseThrow(() -> new NotFoundLibraryEntityException("Aucune bibliothèque disponible"));
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        return Math.sqrt(
                Math.pow(lat1 - lat2, 2) +
                        Math.pow(lon1 - lon2, 2));
    }

    // --- AJOUTER CETTE MÉTHODE ---
    public LibraryEntity saveLibrary(LibraryEntity library) {
        return libraryRepository.save(library);
    }
}
