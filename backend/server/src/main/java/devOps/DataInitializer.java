package devOps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devOps.models.LibraryEntity;
import devOps.repositories.LibraryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LibraryRepository libraryRepository;

    public DataInitializer(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    private static final String JSON_FILE = "repertoire-bibliotheques.json";

    @Override
    public void run(String... args) throws Exception {

        if (libraryRepository.count() > 0) {
            System.out.println("Bibliothèques déjà initialisées.");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        // ⚡ Charger depuis le classpath
        InputStream is = new ClassPathResource(JSON_FILE).getInputStream();

        List<Map<String, Object>> libraries = mapper.readValue(
                is,
                new TypeReference<List<Map<String, Object>>>() {}
        );

        int count = 0;
        for (Map<String, Object> library : libraries) {

            String id = (String) library.get("sql_6");
            if (id == null) continue;

            String name = (String) library.get("nometablissement");
            if (name == null) name = "Nom inconnu";

            String rue = (String) library.get("nomrue");
            String commune = (String) library.get("commune");
            String codePostal = (String) library.get("codepostal");

            String address = String.format("%s, %s %s",
                    rue != null ? rue : "",
                    codePostal != null ? codePostal : "",
                    commune != null ? commune : ""
            );

            Map<String, Object> geo = (Map<String, Object>) library.get("geo");
            if (geo == null || geo.get("lat") == null || geo.get("lon") == null) {
                System.out.println("Bibliothèque ignorée (pas de coordonnées) : " + name);
                continue;
            }

            double latitude = ((Number) geo.get("lat")).doubleValue();
            double longitude = ((Number) geo.get("lon")).doubleValue();

            String heuresOuverture = (String) library.get("heuresouverture");

            LibraryEntity entity = new LibraryEntity();
            entity.setId(id);
            entity.setName(name);
            entity.setAddress(address);
            entity.setLatitude(latitude);
            entity.setLongitude(longitude);
            entity.setHeuresOuverture(heuresOuverture);

            libraryRepository.save(entity);
            count++;
        }

        System.out.println("Initialisation des bibliothèques terminée ! Total : " + count);
    }
}