package devOps.endpoints;

import devOps.dtos.PublicationRequestDTO;
import devOps.dtos.PublicationResponseDTO;
import devOps.services.PublicationService;
import devOps.mappers.PublicationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Publications Communautaires", description = "Endpoints pour gérer les publications")
@RestController
@RequestMapping("/api/publications")
public class PublicationEndpoint {

    private final PublicationService publicationService;
    private final PublicationMapper publicationMapper;

    public PublicationEndpoint(PublicationService publicationService,
                               PublicationMapper publicationMapper) {
        this.publicationService = publicationService;
        this.publicationMapper = publicationMapper;
    }

    @Operation(description = "Créer une nouvelle publication")
    @PostMapping
    public PublicationResponseDTO create(@RequestBody PublicationRequestDTO request) {
        return publicationMapper.toDto(publicationService.create(request));
    }

    @Operation(description = "Récupérer toutes les publications")
    @GetMapping
    public List<PublicationResponseDTO> getAll() {
        return publicationService.getAll().stream()
                .map(publicationMapper::toDto)
                .toList();
    }

    @Operation(description = "Récupérer les publications par utilisateur")
    @GetMapping("/utilisateur/{id}")
    public List<PublicationResponseDTO> getByUtilisateur(@PathVariable Long id) {
        return publicationService.getByUtilisateur(id).stream()
                .map(publicationMapper::toDto)
                .toList();
    }

    @Operation(description = "Récupérer les publications par répondeur")
    @GetMapping("/repondeur/{id}")
    public List<PublicationResponseDTO> getByRepondeur(@PathVariable Long id) {
        return publicationService.getByRepondeur(id).stream()
                .map(publicationMapper::toDto)
                .toList();
    }
}
