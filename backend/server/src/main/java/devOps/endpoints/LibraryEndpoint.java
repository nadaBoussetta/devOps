package devOps.endpoints;

import devOps.dtos.LibraryResponseDTO;
import devOps.errors.NotFoundLibraryErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestion des Bibliothèques", description = "Endpoints pour gérer les bibliothèques")
@RequestMapping("/api/libraries")
public interface LibraryEndpoint {

    @Operation(description = "Récupérer toutes les bibliothèques")
    @ApiResponse(responseCode = "200", description = "Liste de toutes les bibliothèques")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<LibraryResponseDTO> getAllLibraries();

    @Operation(description = "Récupérer une bibliothèque par son ID")
    @ApiResponse(responseCode = "200", description = "Bibliothèque trouvée")
    @ApiResponse(responseCode = "404", description = "Bibliothèque non trouvée",
            content = @Content(schema = @Schema(implementation = NotFoundLibraryErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    LibraryResponseDTO getLibraryById(@PathVariable(name = "id") String id);

    @GetMapping("/recommend")
    LibraryResponseDTO getBestLibrary(@RequestParam double latitude,
                                      @RequestParam double longitude);
}