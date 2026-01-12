package devOps.endpoints;

import devOps.responses.LivreResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import devOps.responses.DisponibiliteResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import devOps.responses.DisponibiliteResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import devOps.responses.DisponibiliteResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@Tag(name = "Recherche Livres", description = "Endpoints pour rechercher des livres disponibles")
@RequestMapping("/api/lieux")
public interface LivreEndpoint {

    @Operation(description = "Recherche des livres disponibles dans un lieu selon un titre")
    @GetMapping("/{lieuId}/livres")
    @ResponseStatus(HttpStatus.OK)
    List<LivreResponseDTO> rechercherLivres(
            @PathVariable(name = "lieuId") String lieuId,
            @RequestParam(name = "titre") String titre
    );

    @GetMapping("/{lieuId}/livres/{livreId}/disponibilite")
    @ResponseStatus(HttpStatus.OK)
    DisponibiliteResponseDTO verifierDisponibilite(@PathVariable String lieuId,
                                                   @PathVariable String livreId);

    @PostMapping("/{lieuId}/livres/{livreId}/emprunter")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void emprunterLivre(@PathVariable String lieuId,
                        @PathVariable String livreId);


}
