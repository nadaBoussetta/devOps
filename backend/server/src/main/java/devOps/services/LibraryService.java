package devOps.services;

import devOps.component.LibraryComponent;
import devOps.mappers.LibraryMapper;
import devOps.models.LibraryEntity;
import devOps.responses.LibraryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryComponent libraryComponent;
    private final LibraryMapper libraryMapper;
    private static final double RAYON_TERRE_KM = 6371.0;

    public List<LibraryResponseDTO> getAllLibraries() {
        return libraryComponent.getAllLibraries()
                .stream()
                .map(libraryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public LibraryResponseDTO getLibrary(String id) {
        return libraryMapper.toResponse(libraryComponent.getLibrary(id));
    }

    public LibraryResponseDTO getBestLibrary(double userLat, double userLon) {
        return libraryMapper.toResponse(libraryComponent.getBestLibrary(userLat, userLon));
    }

    public LibraryResponseDTO createLibrary(LibraryResponseDTO dto) {
        LibraryEntity entity = libraryMapper.toEntity(dto);
        libraryComponent.saveLibrary(entity);
        return libraryMapper.toResponse(entity);
    }

    public List<LibraryResponseDTO> findLibrariesNearDTO(double lat, double lon, double rayonKm) {
        return findLibrariesNear(lat, lon, rayonKm)
                .stream()
                .map(libraryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<String> findLibraryNamesNear(double userLat, double userLon, double rayonKm) {
        return libraryComponent.getAllLibraries()
                .stream()
                .filter(lib -> calculerDistanceKm(userLat, userLon, lib.getLatitude(), lib.getLongitude()) <= rayonKm)
                .sorted((lib1, lib2) -> Double.compare(
                        calculerDistanceKm(userLat, userLon, lib1.getLatitude(), lib1.getLongitude()),
                        calculerDistanceKm(userLat, userLon, lib2.getLatitude(), lib2.getLongitude())))
                .map(LibraryEntity::getName)
                .collect(Collectors.toList());
    }

    private List<LibraryEntity> findLibrariesNear(double userLat, double userLon, double rayonKm) {
        return libraryComponent.getAllLibraries()
                .stream()
                .filter(lib -> calculerDistanceKm(userLat, userLon, lib.getLatitude(), lib.getLongitude()) <= rayonKm)
                .collect(Collectors.toList());
    }

    private double calculerDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAYON_TERRE_KM * c;
    }
}
