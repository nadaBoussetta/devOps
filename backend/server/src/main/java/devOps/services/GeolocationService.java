package devOps.services;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeolocationService {

    private static final Map<String, double[]> ADRESSES_MOCK = new HashMap<>();

    static {
        ADRESSES_MOCK.put("5 rue de la sorbonne, paris", new double[]{48.8489, 2.3436});
        ADRESSES_MOCK.put("10 place du panthéon, paris", new double[]{48.8462, 2.3464});
        ADRESSES_MOCK.put("place de la république, paris", new double[]{48.8676, 2.3634});
        ADRESSES_MOCK.put("tour eiffel, paris", new double[]{48.8584, 2.2945});
        ADRESSES_MOCK.put("arc de triomphe, paris", new double[]{48.8738, 2.2950});
        ADRESSES_MOCK.put("notre-dame, paris", new double[]{48.8530, 2.3499});
        ADRESSES_MOCK.put("gare du nord, paris", new double[]{48.8809, 2.3553});
        ADRESSES_MOCK.put("montparnasse, paris", new double[]{48.8422, 2.3219});
        ADRESSES_MOCK.put("bastille, paris", new double[]{48.8532, 2.3690});
        ADRESSES_MOCK.put("châtelet, paris", new double[]{48.8584, 2.3470});

        ADRESSES_MOCK.put("paris", new double[]{48.8566, 2.3522});
    }

    public double[] geocodeAdresse(String adresse) {
        String adresseNormalisee = normaliserAdresse(adresse);

        if (ADRESSES_MOCK.containsKey(adresseNormalisee)) {
            return ADRESSES_MOCK.get(adresseNormalisee);
        }

        for (Map.Entry<String, double[]> entry : ADRESSES_MOCK.entrySet()) {
            if (adresseNormalisee.contains(entry.getKey()) || entry.getKey().contains(adresseNormalisee)) {
                return entry.getValue();
            }
        }

        return ADRESSES_MOCK.get("paris");
    }

    private String normaliserAdresse(String adresse) {
        return adresse.toLowerCase()
                .trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[,.]", "");
    }
}

