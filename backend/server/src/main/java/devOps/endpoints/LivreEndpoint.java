package devOps.endpoints;

import devOps.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recherche Livres", description = "Endpoints pour rechercher des livres disponibles")
@RequestMapping("/api/lieux")
public interface LivreEndpoint {

    @Operation(description = "Recherche des livres disponibles dans un lieu selon un titre")
    @GetMapping("/{lieuId}/livres")
    @ResponseStatus(HttpStatus.OK)
    List<LivreResponseDTO> rechercherLivres(
            @PathVariable(name = "lieuId") Long lieuId,
            @RequestParam(name = "titre") String titre
    );
}
