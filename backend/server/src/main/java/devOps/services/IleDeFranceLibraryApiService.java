package devOps.services;

import devOps.dtos.IleDeFranceLibraryDTO;
import devOps.util.DistanceCalculator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class IleDeFranceLibraryApiService {

    private static final String API_IDF_URL = "https://data.iledefrance.fr/api/explore/v2.1/catalog/datasets/repertoire-bibliotheques/records";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public IleDeFranceLibraryApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<IleDeFranceLibraryDTO> searchLibraries(double latitude, double longitude, double radius) {
        List<IleDeFranceLibraryDTO> libraries = new ArrayList<>();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_IDF_URL)
                .queryParam("limit", -1); // Récupérer toutes les bibliothèques pour filtrer localement

        JsonNode response = restTemplate.getForObject(builder.toUriString(), JsonNode.class);

        if (response != null && response.has("results") && response.get("results").isArray()) {
            for (JsonNode node : response.get("results")) {
                try {
                    IleDeFranceLibraryDTO library = objectMapper.treeToValue(node, IleDeFranceLibraryDTO.class);
                    if (library.getGeo() != null && library.getGeo().getLat() != null && library.getGeo().getLon() != null) {
                        double distance = DistanceCalculator.calculateDistance(
                                latitude, longitude,
                                library.getGeo().getLat(), library.getGeo().getLon()
                        );
                        if (distance <= radius) {
                            libraries.add(library);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing IleDeFranceLibraryDTO: " + e.getMessage());
                }
            }
        }
        return libraries;
    }
}