package devOps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devOps.models.LibraryEntity;
import devOps.repositories.LibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final LibraryRepository libraryRepository;

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ClassPathResource resource = new ClassPathResource("repertoire-bibliotheques.json");

        if (!resource.exists()) {
            System.err.println(" Fichier JSON introuvable dans les resources !");
            return;
        }

        System.out.println(" Chargement des bibliothèques depuis " + resource.getFilename());

        // Lire le JSON
        List<Map<String, Object>> libraries = mapper.readValue(
                resource.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {});

        int count = 0;
        for (Map<String, Object> library : libraries) {
            String name = (String) library.get("nometablissement");
            String address = (String) library.get("nomrue");

            Map<String, Object> geo = (Map<String, Object>) library.get("geo");
            if (geo == null || geo.get("lat") == null || geo.get("lon") == null) {
                System.out.println("⚠️ Bibliothèque ignorée (pas de geo) : " + name);
                continue;
            }

            double latitude = ((Number) geo.get("lat")).doubleValue();
            double longitude = ((Number) geo.get("lon")).doubleValue();

            LibraryEntity entity = new LibraryEntity();
            entity.setName(name);
            entity.setAddress(address);
            entity.setLatitude(latitude);
            entity.setLongitude(longitude);

            libraryRepository.save(entity);
            count++;
        }

        System.out.println("Initialisation terminée ! " + count + " bibliothèques chargées.");
    }
}