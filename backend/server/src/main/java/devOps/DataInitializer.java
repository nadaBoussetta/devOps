package devOps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devOps.models.LibraryEntity;
import devOps.repositories.LibraryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LibraryRepository libraryRepository;

    // Constructeur explicite pour Spring
    public DataInitializer(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    private static final String JSON_FILE_PATH =
            "src/main/resources/repertoire-bibliotheques.json";

    @Override
    public void run(String... args) throws Exception {

        // Évite de recharger si la base est déjà remplie
        if (libraryRepository.count() > 0) {
            System.out.println("Bibliothèques déjà initialisées.");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> libraries = mapper.readValue(
                new File(JSON_FILE_PATH),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        for (Map<String, Object> library : libraries) {

            // ===== ID =====
            String id = (String) library.get("sql_6");
            if (id == null) continue;

            // ===== Nom =====
            String name = (String) library.get("nometablissement");
            if (name == null) name = "Nom inconnu";

            // ===== Adresse complète =====
            String rue = (String) library.get("nomrue");
            String commune = (String) library.get("commune");
            String codePostal = (String) library.get("codepostal");

            String address = String.format("%s, %s %s",
                    rue != null ? rue : "",
                    codePostal != null ? codePostal : "",
                    commune != null ? commune : ""
            );

            // ===== Geo =====
            Map<String, Object> geo = (Map<String, Object>) library.get("geo");
            if (geo == null || geo.get("lat") == null || geo.get("lon") == null) {
                System.out.println("Bibliothèque ignorée (pas de coordonnées) : " + name);
                continue;
            }

            double latitude = ((Number) geo.get("lat")).doubleValue();
            double longitude = ((Number) geo.get("lon")).doubleValue();

            // ===== Horaires =====
            String heuresOuverture = (String) library.get("heuresouverture");

            // ===== Création entité =====
            LibraryEntity entity = new LibraryEntity();
            entity.setId(id);
            entity.setName(name);
            entity.setAddress(address);
            entity.setLatitude(latitude);
            entity.setLongitude(longitude);
            entity.setHeuresOuverture(heuresOuverture);

            libraryRepository.save(entity);
        }

        System.out.println(" Initialisation des bibliothèques terminée !");
    }
}
