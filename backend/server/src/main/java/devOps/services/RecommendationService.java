package devOps.services;

import devOps.dtos.*;
import devOps.enums.TypeBibliotheque;
import devOps.models.*;
import devOps.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private NotationRepository notationRepository;

    @Autowired
    private FavoriRepository favoriRepository;

    @Autowired
    private LibraryRepository bibliothequeRepository;

    @Autowired
    private LibraryService bibliothequeService;

    public List<LibraryResponseDTO> getRecommendations(Long userId) {
        List<NotationEntity> notations = notationRepository.findByUserIdOrderByDateNotationDesc(userId);
        List<FavoriEntity> favoris = favoriRepository.findByUser_IdOrderByDateAjoutDesc(userId);

        Set<Long> bibliothequesBienNotees = notations.stream()
                .filter(n -> n.getNote() >= 3)
                .map(n -> n.getBibliotheque().getId())
                .collect(Collectors.toSet());

        Set<Long> bibliothequesEnFavori = favoris.stream()
                .map(f -> f.getLibraryEntity().getId())
                .collect(Collectors.toSet());

        Map<TypeBibliotheque, Integer> typesPreferences = new HashMap<>();
        for (NotationEntity notation : notations) {
            if (notation.getNote() >= 3) {
                TypeBibliotheque type = notation.getBibliotheque().getType();
                typesPreferences.put(type, typesPreferences.getOrDefault(type, 0) + 1);
            }
        }

        TypeBibliotheque typePreference = typesPreferences.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        List<LibraryEntity> toutesBibliotheques = bibliothequeRepository.findAll();

        List<BibliothequeScore> scores = new ArrayList<>();

        for (LibraryEntity biblio : toutesBibliotheques) {
            if (bibliothequesBienNotees.contains(biblio.getId()) ||
                    bibliothequesEnFavori.contains(biblio.getId())) {
                continue;
            }

            double score = calculateScore(biblio, typePreference);
            scores.add(new BibliothequeScore(biblio, score));
        }

        scores.sort(Comparator.comparingDouble(BibliothequeScore::getScore).reversed());

        return scores.stream()
                .limit(10)
                .map(bs -> bibliothequeService.getBibliothequeById(bs.getBibliotheque().getId()))
                .collect(Collectors.toList());
    }

    private double calculateScore(LibraryEntity bibliotheque, TypeBibliotheque typePreference) {
        double score = 0.0;

        score += bibliotheque.getNoteGlobale();

        if (typePreference != null && bibliotheque.getType() == typePreference) {
            score += 3.0;
        }

        if (bibliotheque.getNombreNotations() > 10) {
            score += 2.0;
        } else if (bibliotheque.getNombreNotations() > 5) {
            score += 1.0;
        }

        return score;
    }

    private static class BibliothequeScore {
        private final LibraryEntity bibliotheque;
        private final double score;

        public BibliothequeScore(LibraryEntity bibliotheque, double score) {
            this.bibliotheque = bibliotheque;
            this.score = score;
        }

        public LibraryEntity getBibliotheque() {
            return bibliotheque;
        }

        public double getScore() {
            return score;
        }
    }
}