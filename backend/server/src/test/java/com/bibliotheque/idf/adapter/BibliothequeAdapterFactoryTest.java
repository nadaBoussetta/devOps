package devOps.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BibliothequeAdapterFactoryTest {

    private BibliothequeAdapterFactory createFactory() {
        RestTemplate restTemplate = new RestTemplate();

        SorbonneAdapter sorbonne = new SorbonneAdapter(restTemplate);
        ParisDescarteAdapter parisDescartes = new ParisDescarteAdapter(restTemplate);

        Map<String, LibraryAdapter> map = new HashMap<>();
        map.put("sorbonne", sorbonne);
        map.put("paris-descartes", parisDescartes);

        return new BibliothequeAdapterFactory(map);
    }

    @Test
    void getAdapter_retourneBonAdapterPourIdsConnus() {
        // GIVEN
        BibliothequeAdapterFactory factory = createFactory();

        // WHEN
        LibraryAdapter sorbonne = factory.getAdapter("sorbonne");
        LibraryAdapter parisDescartes = factory.getAdapter("paris-descartes");

        // THEN
        assertNotNull(sorbonne);
        assertNotNull(parisDescartes);
        assertTrue(sorbonne instanceof SorbonneAdapter);
        assertTrue(parisDescartes instanceof ParisDescarteAdapter);
    }

    @Test
    void getAdapter_idInconnu_declencheException() {
        // GIVEN
        BibliothequeAdapterFactory factory = createFactory();

        // WHEN + THEN
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> factory.getAdapter("bibliotheque-inconnue")
        );

        assertTrue(ex.getMessage().contains("Aucun adaptateur"),
                "Le message doit mentionner qu'aucun adaptateur n'a été trouvé");
    }
}