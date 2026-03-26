package devOps.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IleDeFranceLibraryDTO {

    @JsonProperty("nometablissement")
    private String nomEtablissement;

    @JsonProperty("nomrue")
    private String nomRue;

    @JsonProperty("commune")
    private String commune;

    @JsonProperty("codepostal")
    private String codePostal;

    @JsonProperty("geo")
    private GeoCoordinates geo;

    @JsonProperty("typeinst")
    private String typeInst;

    @JsonProperty("heuresouverture")
    private String heuresOuverture;

    @JsonProperty("services_proposes")
    private List<String> servicesProposes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeoCoordinates {
        @JsonProperty("lon")
        private Double lon;
        @JsonProperty("lat")
        private Double lat;
    }
}