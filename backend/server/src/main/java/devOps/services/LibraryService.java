package devOps.services;

import devOps.dtos.LibraryResponseDTO;
import devOps.dtos.HoraireDTO;
import devOps.dtos.RechercheDTO;
import devOps.dtos.IleDeFranceLibraryDTO;
import devOps.enums.TypeBibliotheque;
import devOps.models.LibraryEntity;
import devOps.models.HoraireEntity;
import devOps.repositories.LibraryRepository;
import devOps.util.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class LibraryService {

    @Autowired(required = false) // Rendre l'injection optionnelle si la base de données locale n'est plus utilisée pour la recherche
    private LibraryRepository libraryRepository;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private IleDeFranceLibraryApiService ileDeFranceLibraryApiService;

    public List<LibraryResponseDTO> rechercherBibliotheques(RechercheDTO recherche) {

        double[] coordonnees = geolocationService.geocodeAdresse(recherche.getAdresse());
        double latRecherche = coordonnees[0];
        double lonRecherche = coordonnees[1];

        // Recherche dans l'API Ile-de-France
        List<IleDeFranceLibraryDTO> idfLibraries = ileDeFranceLibraryApiService.searchLibraries(latRecherche, lonRecherche, recherche.getRayon());
        List<LibraryResponseDTO> resultats = new ArrayList<>();

        for (IleDeFranceLibraryDTO idfLib : idfLibraries) {
            LibraryResponseDTO dto = convertIleDeFranceToDTO(idfLib, latRecherche, lonRecherche);
            resultats.add(dto);
        }

        // La logique de tri peut être appliquée ici si nécessaire
        resultats.sort(Comparator
                .comparing(LibraryResponseDTO::getOuvert, Comparator.reverseOrder())
                .thenComparing(LibraryResponseDTO::getDistance)
                .thenComparing(LibraryResponseDTO::getNoteGlobale, Comparator.reverseOrder())
        );

        return resultats;
    }

    // Cette méthode n'est plus utilisée pour les bibliothèques externes, mais peut être conservée pour d'autres fonctionnalités
    private boolean verifierOuverture(LibraryEntity lib, LocalTime debut, LocalTime fin) {
        if (lib.getHoraires() == null) return false;
        for (HoraireEntity horaire : lib.getHoraires()) {
            if (horaire.estOuvertSur(debut, fin)) {
                return true;
            }
        }
        return false;
    }

    public List<LibraryResponseDTO> getAllBibliotheques() {
        if (libraryRepository != null) {
            return libraryRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>(); // Retourne une liste vide si le repository n'est pas disponible
    }

    public LibraryResponseDTO getBibliothequeById(Long id) {
        if (libraryRepository != null) {
            LibraryEntity lib = libraryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));
            return convertToDTO(lib);
        }
        throw new UnsupportedOperationException("La recherche par ID n'est pas supportée pour les bibliothèques externes.");
    }

    private LibraryResponseDTO convertToDTO(LibraryEntity lib) {
        LibraryResponseDTO dto = new LibraryResponseDTO();
        dto.setId(lib.getId());
        dto.setNom(lib.getNom());
        dto.setAdresse(lib.getAdresse());
        dto.setLatitude(lib.getLatitude());
        dto.setLongitude(lib.getLongitude());
        dto.setType(lib.getType());
        dto.setNoteGlobale(lib.getNoteGlobale());
        dto.setNombreNotations(lib.getNombreNotations());

        List<HoraireDTO> horairesDTO = lib.getHoraires().stream()
                .map(this::convertHoraireToDTO)
                .collect(Collectors.toList());
        dto.setHoraires(horairesDTO);

        return dto;
    }

    private HoraireDTO convertHoraireToDTO(HoraireEntity horaire) {
        HoraireDTO dto = new HoraireDTO();
        dto.setId(horaire.getId());
        dto.setJourSemaine(horaire.getJourSemaine());
        dto.setHeureOuverture(horaire.getHeureOuverture().toString());
        dto.setHeureFermeture(horaire.getHeureFermeture().toString());
        return dto;
    }

    private LibraryResponseDTO convertIleDeFranceToDTO(IleDeFranceLibraryDTO idfLib, double latRecherche, double lonRecherche) {
        LibraryResponseDTO dto = new LibraryResponseDTO();
        // Générer un ID unique pour les bibliothèques externes
        dto.setId(Math.abs((long) UUID.randomUUID().hashCode())); // Utiliser un hash de UUID pour un Long
        dto.setNom(idfLib.getNomEtablissement());
        dto.setAdresse(idfLib.getNomRue() + ", " + idfLib.getCodePostal() + " " + idfLib.getCommune());
        dto.setLatitude(idfLib.getGeo().getLat());
        dto.setLongitude(idfLib.getGeo().getLon());

        // Mapper le type de bibliothèque
        try {
            // L'API IDF peut avoir des types plus génériques, nous essayons de les mapper à nos enums
            if (idfLib.getTypeInst() != null && idfLib.getTypeInst().toLowerCase().contains("universitaire")) {
                dto.setType(TypeBibliotheque.UNIVERSITAIRE);
            } else {
                dto.setType(TypeBibliotheque.PUBLIQUE);
            }
        } catch (IllegalArgumentException e) {
            dto.setType(TypeBibliotheque.PUBLIQUE); // Type par défaut si le mapping échoue
        }

        dto.setNoteGlobale(0.0); // Les API externes n'ont pas de notes globales
        dto.setNombreNotations(0); // Les API externes n'ont pas de nombre de notations

        // Calculer la distance
        double distance = DistanceCalculator.calculateDistance(
                latRecherche, lonRecherche,
                idfLib.getGeo().getLat(), idfLib.getGeo().getLon()
        );
        dto.setDistance(Math.round(distance * 100.0) / 100.0);

        // Gérer les horaires d'ouverture (l'API IDF fournit une chaîne de caractères)
        // Pour l'instant, nous ne pouvons pas déterminer l'ouverture avec précision sans une logique de parsing complexe.
        dto.setOuvert(false); // Par défaut à false
        dto.setHoraires(new ArrayList<>()); // Pas d'horaires structurés directement disponibles

        return dto;
    }
}