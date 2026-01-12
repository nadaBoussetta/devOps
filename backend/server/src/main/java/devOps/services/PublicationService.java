package devOps.services;

import devOps.dtos.PublicationRequestDTO;
import devOps.dtos.PublicationResponseDTO;
import devOps.models.PublicationEntity;
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

    public PublicationEntity create(PublicationRequestDTO request) {

        PublicationEntity publication = new PublicationEntity();
        publication.setMessage(request.getMessage());
        publication.setDate(new Date());

        publication.setUtilisateur(
                utilisateurRepository.findById(request.getUtilisateurId())
                        .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"))
        );

        if (request.getRepondeurId() != null) {
            publication.setRepondeur(
                    utilisateurRepository.findById(request.getRepondeurId())
                            .orElseThrow(() -> new RuntimeException("Répondeur introuvable"))
            );
        }

        return publicationRepository.save(publication);
    }

    public List<PublicationEntity> getAll() {
        return publicationRepository.findAllByOrderByDateDesc();
    }

    public List<PublicationEntity> getByUtilisateur(Long utilisateurId) {
        return publicationRepository.findByUtilisateurIdOrderByDateDesc(utilisateurId);
    }

    public List<PublicationEntity> getByRepondeur(Long repondeurId) {
        return publicationRepository.findByRepondeurId(repondeurId);
    }
}
