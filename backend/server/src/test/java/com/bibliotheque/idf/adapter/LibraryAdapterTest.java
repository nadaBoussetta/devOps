package devOps.adapter;

import devOps.dtos.LivreResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryAdapterContractTest {

    @Test
    void sorbonneAdapter_respectsInterfaceContract() {
        LibraryAdapter adapter = new SorbonneAdapter(new RestTemplate());

        assertNotNull(adapter.getNomBibliotheque());
        assertFalse(adapter.getNomBibliotheque().isBlank());

        List<LivreResponseDTO> result =
                adapter.rechercherLivre("titre-inexistant");

        assertNotNull(result);
    }

    @Test
    void parisDescartesAdapter_respectsInterfaceContract() {
        LibraryAdapter adapter = new ParisDescarteAdapter(new RestTemplate());

        assertNotNull(adapter.getNomBibliotheque());
        assertFalse(adapter.getNomBibliotheque().isBlank());

        List<LivreResponseDTO> result =
                adapter.rechercherLivre("titre-inexistant");

        assertNotNull(result);
    }
}