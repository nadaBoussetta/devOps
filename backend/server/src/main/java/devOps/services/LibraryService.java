package devOps.services;

import devOps.component.LibraryComponent;
import devOps.mappers.LibraryMapper;
import devOps.models.LibraryEntity;
import devOps.responses.LibraryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryComponent libraryComponent;
    private final LibraryMapper libraryMapper;

    public List<LibraryResponseDTO> getAllLibraries() {
        return libraryComponent.getAllLibraries()
                .stream()
                .map(libraryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public LibraryResponseDTO getLibrary(String id) {
        LibraryEntity libraryEntity = libraryComponent.getLibrary(id);
        return libraryMapper.toResponse(libraryEntity);
    }


    public LibraryResponseDTO getBestLibrary(double userLat, double userLon) {
        LibraryEntity bestLibrary = libraryComponent.getBestLibrary(userLat, userLon);
        return libraryMapper.toResponse(bestLibrary);
    }
}
