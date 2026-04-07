package devOps.controllers;

import devOps.services.AddressAutocompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour les endpoints d'adresses (autocomplétion, géolocalisation, etc.)
 */
@RestController
@RequestMapping("/api/adresses")
@CrossOrigin(origins = "*")
public class AdressController {

    @Autowired
    private AddressAutocompleteService addressAutocompleteService;

    /**
     * Endpoint pour l'autocomplétion d'adresses.
     *
     * @param query texte de recherche
     * @param limit nombre maximum de résultats (par défaut 5)
     * @return liste des adresses suggérées
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<AddressAutocompleteService.AddressSuggestion>> autocomplete(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {

        List<AddressAutocompleteService.AddressSuggestion> suggestions =
                addressAutocompleteService.getAddressSuggestions(query, limit);

        return ResponseEntity.ok(suggestions);
    }
}