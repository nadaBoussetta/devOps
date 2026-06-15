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

    @org.springframework.beans.factory.annotation.Autowired
    private devOps.repositories.CachedLibraryRepository cachedLibraryRepository;

    public List<IleDeFranceLibraryDTO> searchLibraries(double latitude, double longitude, double radius) {
        List<devOps.models.CachedLibraryEntity> cached = cachedLibraryRepository.findAll();
        if (cached.isEmpty()) {
            updateCache();
            cached = cachedLibraryRepository.findAll();
        }

        List<IleDeFranceLibraryDTO> libraries = new ArrayList<>();
        for (devOps.models.CachedLibraryEntity entity : cached) {
            if (entity.getLatitude() != null && entity.getLongitude() != null) {
                double distance = DistanceCalculator.calculateDistance(
                        latitude, longitude,
                        entity.getLatitude(), entity.getLongitude()
                );
                if (distance <= radius) {
                    libraries.add(convertToDTO(entity));
                }
            }
        }
        return libraries;
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 3 * * *") // Tous les jours à 3h du matin
    public void updateCache() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_IDF_URL)
                .queryParam("limit", -1);

        JsonNode response = restTemplate.getForObject(builder.toUriString(), JsonNode.class);

        if (response != null && response.has("results") && response.get("results").isArray()) {
            cachedLibraryRepository.deleteAll();
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            for (JsonNode node : response.get("results")) {
                try {
                    IleDeFranceLibraryDTO dto = objectMapper.treeToValue(node, IleDeFranceLibraryDTO.class);
                    devOps.models.CachedLibraryEntity entity = new devOps.models.CachedLibraryEntity();
                    entity.setNomEtablissement(dto.getNomEtablissement());
                    entity.setNomRue(dto.getNomRue());
                    entity.setCodePostal(dto.getCodePostal());
                    entity.setCommune(dto.getCommune());
                    entity.setTypeInst(dto.getTypeInst());
                    entity.setHeuresOuverture(dto.getHeuresOuverture());
                    if (dto.getGeo() != null) {
                        entity.setLatitude(dto.getGeo().getLat());
                        entity.setLongitude(dto.getGeo().getLon());
                    }
                    entity.setLastUpdated(now);
                    cachedLibraryRepository.save(entity);
                } catch (Exception e) {
                    System.err.println("Error caching library: " + e.getMessage());
                }
            }
        }
    }

    private IleDeFranceLibraryDTO convertToDTO(devOps.models.CachedLibraryEntity entity) {
        IleDeFranceLibraryDTO dto = new IleDeFranceLibraryDTO();
        dto.setNomEtablissement(entity.getNomEtablissement());
        dto.setNomRue(entity.getNomRue());
        dto.setCodePostal(entity.getCodePostal());
        dto.setCommune(entity.getCommune());
        dto.setTypeInst(entity.getTypeInst());
        dto.setHeuresOuverture(entity.getHeuresOuverture());
        IleDeFranceLibraryDTO.GeoCoordinates geo = new IleDeFranceLibraryDTO.GeoCoordinates();
        geo.setLat(entity.getLatitude());
        geo.setLon(entity.getLongitude());
        dto.setGeo(geo);
        return dto;
    }
}