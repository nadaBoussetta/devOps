package devOps.services;
import devOps.dtos.LibraryResponseDTO;
import devOps.dtos.HoraireDTO;
import devOps.dtos.RechercheDTO;
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
import java.util.stream.Collectors;


@Service
public class LibraryService {

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private GeolocationService geolocationService;

    public List<LibraryResponseDTO> rechercherBibliotheques(RechercheDTO recherche) {

        double[] coordonnees = geolocationService.geocodeAdresse(recherche.getAdresse());
        double latRecherche = coordonnees[0];
        double lonRecherche = coordonnees[1];

        LocalTime heureDebut = LocalTime.parse(recherche.getHeureDebut());
        LocalTime heureFin = LocalTime.parse(recherche.getHeureFin());

        List<LibraryEntity> toutesBibliotheques = libraryRepository.findAll();

        List<LibraryResponseDTO> resultats = new ArrayList<>();

        for (LibraryEntity lib : toutesBibliotheques) {
            double distance = DistanceCalculator.calculateDistance(
                    latRecherche, lonRecherche,
                    lib.getLatitude(), lib.getLongitude()
            );

            if (distance <= recherche.getRayon()) {
                boolean ouvert = verifierOuverture(lib, heureDebut, heureFin);

                LibraryResponseDTO dto = convertToDTO(lib);
                dto.setDistance(Math.round(distance * 100.0) / 100.0);
                dto.setOuvert(ouvert);

                resultats.add(dto);
            }
        }

        resultats.sort(Comparator
                .comparing(LibraryResponseDTO::getOuvert, Comparator.reverseOrder())
                .thenComparing(LibraryResponseDTO::getDistance)
                .thenComparing(LibraryResponseDTO::getNoteGlobale, Comparator.reverseOrder())
        );

        return resultats;
    }

    private boolean verifierOuverture(LibraryEntity lib, LocalTime debut, LocalTime fin) {
        for (HoraireEntity horaire : lib.getHoraires()) {
            if (horaire.estOuvertSur(debut, fin)) {
                return true;
            }
        }
        return false;
    }

    public List<LibraryResponseDTO> getAllBibliotheques() {
        return libraryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LibraryResponseDTO getBibliothequeById(Long id) {
        LibraryEntity lib = libraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));
        return convertToDTO(lib);
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
}