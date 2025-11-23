package devOps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devOps.models.LibraryEntity;
import devOps.repositories.LibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final LibraryRepository libraryRepository;

    private static final String JSON_FILE_PATH = "src/main/resources/repertoire-bibliotheques.json";

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Lire le JSON
        List<Map<String, Object>> libraries = mapper.readValue(
                new File(JSON_FILE_PATH),
                new TypeReference<List<Map<String, Object>>>() {});

        for (Map<String, Object> library : libraries) {
            String name = (String) library.get("nometablissement");
            String address = (String) library.get("nomrue"); // <-- champ correct pour ton JSON

            Map<String, Object> geo = (Map<String, Object>) library.get("geo");
            if (geo == null || geo.get("lat") == null || geo.get("lon") == null) {
                System.out.println("Bibliothèque ignorée (pas de geo) : " + name);
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
        }

        System.out.println("Initialisation des bibliothèques terminée !");
    }
}
