package devOps.services;

import devOps.dtos.*;
import devOps.models.*;
import devOps.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotationService {

    @Autowired
    private NotationRepository notationRepository;

    @Autowired
    private FavoriRepository favoriRepository;

    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private LibraryRepository bibliothequeRepository;

    @Transactional
    public NotationDTO noterBibliotheque(NotationDTO notationDTO, Long userId) {
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        LibraryEntity bibliotheque = bibliothequeRepository.findById(notationDTO.getBibliothequeId())
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));

        NotationEntity notation = notationRepository
                .findByUserIdAndBibliothequeId(userId, notationDTO.getBibliothequeId())
                .orElse(new NotationEntity());

        notation.setUser(user);
        notation.setBibliotheque(bibliotheque);
        notation.setNote(notationDTO.getNote());
        notation.setCommentaire(notationDTO.getCommentaire());
        notation.setDateVisite(notationDTO.getDateVisite());

        NotationEntity savedNotation = notationRepository.save(notation);

        updateNoteGlobaleBibliotheque(bibliotheque);

        return convertNotationToDTO(savedNotation);
    }

    public List<NotationDTO> getNotationsByUser(Long userId) {
        return notationRepository.findByUserIdOrderByDateNotationDesc(userId).stream()
                .map(this::convertNotationToDTO)
                .collect(Collectors.toList());
    }


    public List<NotationDTO> getNotationsByBibliotheque(Long bibliothequeId) {
        return notationRepository.findByBibliothequeIdOrderByDateNotationDesc(bibliothequeId).stream()
                .map(this::convertNotationToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public FavoriDTO ajouterFavori(Long bibliothequeId, Long userId) {
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        LibraryEntity bibliotheque = bibliothequeRepository.findById(bibliothequeId)
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));

        if (favoriRepository.existsByUser_IdAndLibraryEntity_Id(userId, bibliothequeId)) {
            throw new RuntimeException("Cette bibliothèque est déjà dans vos favoris");
        }

        FavoriEntity favori = new FavoriEntity();
        favori.setUser(user);
        favori.setLibraryEntity(bibliotheque);

        FavoriEntity savedFavori = favoriRepository.save(favori);
        return convertFavoriToDTO(savedFavori);
    }

    @Transactional
    public void supprimerFavori(Long bibliothequeId, Long userId) {
        if (!favoriRepository.existsByUser_IdAndLibraryEntity_Id(userId, bibliothequeId)) {
            throw new RuntimeException("Ce favori n'existe pas");
        }

        favoriRepository.deleteByUser_IdAndLibraryEntity_Id(userId, bibliothequeId);
    }

    public List<FavoriDTO> getFavorisByUser(Long userId) {
        return favoriRepository.findByUser_IdOrderByDateAjoutDesc(userId).stream()
                .map(this::convertFavoriToDTO)
                .collect(Collectors.toList());
    }

    private void updateNoteGlobaleBibliotheque(LibraryEntity bibliotheque) {
        List<NotationEntity> notations = notationRepository.findByBibliothequeIdOrderByDateNotationDesc(bibliotheque.getId());

        if (!notations.isEmpty()) {
            double moyenne = notations.stream()
                    .mapToInt(NotationEntity::getNote)
                    .average()
                    .orElse(0.0);

            bibliotheque.setNoteGlobale(Math.round(moyenne * 10.0) / 10.0);
            bibliotheque.setNombreNotations(notations.size());
            bibliothequeRepository.save(bibliotheque);
        }
    }


    private NotationDTO convertNotationToDTO(NotationEntity notation) {
        NotationDTO dto = new NotationDTO();
        dto.setId(notation.getId());
        dto.setBibliothequeId(notation.getBibliotheque().getId());
        dto.setBibliothequeNom(notation.getBibliotheque().getNom());
        dto.setNote(notation.getNote());
        dto.setCommentaire(notation.getCommentaire());
        dto.setDateVisite(notation.getDateVisite());
        dto.setDateNotation(notation.getDateNotation());
        return dto;
    }

    private FavoriDTO convertFavoriToDTO(FavoriEntity favori) {
        FavoriDTO dto = new FavoriDTO();
        dto.setId(favori.getId());
        dto.setBibliothequeId(favori.getLibraryEntity().getId());
        dto.setBibliothequeNom(favori.getLibraryEntity().getNom());
        dto.setDateAjout(favori.getDateAjout());
        return dto;
    }
}