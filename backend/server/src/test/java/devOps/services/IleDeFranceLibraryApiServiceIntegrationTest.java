package devOps.services;

import devOps.dtos.IleDeFranceLibraryDTO;
import devOps.models.CachedLibraryEntity;
import devOps.repositories.CachedLibraryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class IleDeFranceLibraryApiServiceIntegrationTest {

    @Autowired
    private IleDeFranceLibraryApiService apiService;

    @Autowired
    private CachedLibraryRepository cachedLibraryRepository;

    @Test
    void testSearchLibrariesUsesCache() {
        // Préparer le cache
        cachedLibraryRepository.deleteAll();
        CachedLibraryEntity entity = new CachedLibraryEntity();
        entity.setNomEtablissement("Ma Biblio de Test");
        entity.setLatitude(48.8566);
        entity.setLongitude(2.3522);
        cachedLibraryRepository.save(entity);

        // Appeler le service (devrait utiliser le cache sans appeler l'API si le cache n'est pas vide)
        List<IleDeFranceLibraryDTO> results = apiService.searchLibraries(48.8566, 2.3522, 10.0);

        assertFalse(results.isEmpty());
        assertEquals("Ma Biblio de Test", results.get(0).getNomEtablissement());
    }
}
