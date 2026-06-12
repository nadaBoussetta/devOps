package devOps.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devOps.dtos.FavoriDTO;
import devOps.dtos.NotationDTO;
import devOps.models.FavoriEntity;
import devOps.models.LibraryEntity;
import devOps.models.NotationEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.FavoriRepository;
import devOps.repositories.LibraryRepository;
import devOps.repositories.NotationRepository;
import devOps.repositories.UtilisateurRepository;

@Service
public class NotationService {

    @Autowired private NotationRepository notationRepository;
    @Autowired private FavoriRepository favoriRepository;
    @Autowired private UtilisateurRepository userRepository;
    @Autowired private LibraryRepository bibliothequeRepository;

    @Transactional
    public NotationDTO noterBibliotheque(NotationDTO dto, Long userId) {
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        LibraryEntity biblio = bibliothequeRepository.findById(dto.getBibliothequeId())
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));

        NotationEntity notation = notationRepository
                .findByUserIdAndBibliothequeId(userId, dto.getBibliothequeId())
                .orElse(new NotationEntity());

        notation.setUser(user);
        notation.setBibliotheque(biblio);
        notation.setNote(dto.getNote());
        notation.setCommentaire(dto.getCommentaire());
        notation.setDateVisite(dto.getDateVisite());

        NotationEntity saved = notationRepository.save(notation);

        // Recalculer la note globale
        List<NotationEntity> toutes = notationRepository.findByBibliothequeIdOrderByDateNotationDesc(biblio.getId());
        double avg = toutes.stream().mapToInt(NotationEntity::getNote).average().orElse(0);
        biblio.setNoteGlobale(avg);
        biblio.setNombreNotations(toutes.size());
        bibliothequeRepository.save(biblio);

        return convertToDTO(saved);
    }

    public List<NotationDTO> getNotationsByUser(Long userId) {
        return notationRepository.findByUserIdOrderByDateNotationDesc(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<NotationDTO> getNotationsByBibliotheque(Long bibliothequeId) {
        return notationRepository.findByBibliothequeIdOrderByDateNotationDesc(bibliothequeId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void supprimerNotation(Long notationId, Long userId) {
        NotationEntity notation = notationRepository.findById(notationId)
                .orElseThrow(() -> new RuntimeException("Notation non trouvée"));
        if (!notation.getUser().getId().equals(userId)) {
            throw new RuntimeException("Non autorisé");
        }
        notationRepository.delete(notation);
    }

    // ─── Favoris ──────────────────────────────────────────────────────────────

    @Transactional
    public FavoriDTO ajouterFavori(Long bibliothequeId, Long userId) {
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        LibraryEntity biblio = bibliothequeRepository.findById(bibliothequeId)
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));

        if (favoriRepository.existsByUser_IdAndLibraryEntity_Id(userId, bibliothequeId)) {
            throw new RuntimeException("Déjà en favoris");
        }

        FavoriEntity favori = new FavoriEntity();
        favori.setUser(user);
        favori.setLibraryEntity(biblio);
        FavoriEntity saved = favoriRepository.save(favori);
        return convertFavoriToDTO(saved);
    }

    @Transactional
    public void supprimerFavori(Long bibliothequeId, Long userId) {
        favoriRepository.deleteByUser_IdAndLibraryEntity_Id(userId, bibliothequeId);
    }

    public List<FavoriDTO> getFavorisByUser(Long userId) {
        return favoriRepository.findByUser_IdOrderByDateAjoutDesc(userId)
                .stream().map(this::convertFavoriToDTO).collect(Collectors.toList());
    }

    // ─── Utilitaires ──────────────────────────────────────────────────────────

    private NotationDTO convertToDTO(NotationEntity n) {
        NotationDTO dto = new NotationDTO();
        dto.setId(n.getId());
        dto.setBibliothequeId(n.getBibliotheque().getId());
        dto.setBibliothequeNom(n.getBibliotheque().getNom());
        dto.setNote(n.getNote());
        dto.setCommentaire(n.getCommentaire());
        dto.setDateVisite(n.getDateVisite());
        dto.setDateNotation(n.getDateNotation());
        return dto;
    }

    private FavoriDTO convertFavoriToDTO(FavoriEntity f) {
        FavoriDTO dto = new FavoriDTO();
        dto.setId(f.getId());
        dto.setBibliothequeId(f.getLibraryEntity().getId());
        dto.setBibliothequeNom(f.getLibraryEntity().getNom());
        dto.setDateAjout(f.getDateAjout());
        return dto;
    }
}
