package devOps.services;

import devOps.dtos.PublicationRequestDTO;
import devOps.models.PublicationEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.PublicationRepository;
import devOps.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public PublicationService(PublicationRepository publicationRepository,
                              UtilisateurRepository utilisateurRepository) {
        this.publicationRepository = publicationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public PublicationEntity createPublication(PublicationRequestDTO dto) {

        UtilisateurEntity utilisateur = utilisateurRepository.findById(dto.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        UtilisateurEntity repondeur = null;
        if (dto.getRepondeurId() != null) {
            repondeur = utilisateurRepository.findById(dto.getRepondeurId())
                    .orElse(null);
        }

        PublicationEntity pub = new PublicationEntity();
        pub.setMessage(dto.getMessage());
        pub.setDate(new Date());
        pub.setUtilisateur(utilisateur);
        pub.setRepondeur(repondeur);

        return publicationRepository.save(pub);
    }

    public List<PublicationEntity> getAllPublications() {
        return publicationRepository.findAll();
    }

    public PublicationEntity getPublication(Long id) {
        return publicationRepository.findById(id).orElse(null);
    }

    public void deletePublication(Long id) {
        publicationRepository.deleteById(id);
    }
}
