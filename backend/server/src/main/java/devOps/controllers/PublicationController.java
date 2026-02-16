package devOps.controllers;

import devOps.dtos.PublicationRequestDTO;
import devOps.models.PublicationEntity;
import devOps.services.PublicationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:63342")
@RestController
@RequestMapping("/publications")
public class PublicationController {

    private final PublicationService publicationService;

    public PublicationController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    // CREATE
    @PostMapping
    public PublicationEntity createPublication(@RequestBody PublicationRequestDTO dto) {
        return publicationService.createPublication(dto);
    }

    // GET ALL
    @GetMapping
    public List<PublicationEntity> getAllPublications() {
        return publicationService.getAllPublications();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public PublicationEntity getPublication(@PathVariable Long id) {
        return publicationService.getPublication(id);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deletePublication(@PathVariable Long id) {
        publicationService.deletePublication(id);
    }
}
