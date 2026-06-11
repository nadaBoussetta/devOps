package devOps.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devOps.dtos.CommentDTO;
import devOps.dtos.PublicationDTO;
import devOps.dtos.ReactionDTO;
import devOps.enums.ReactionType;
import devOps.models.CommentEntity;
import devOps.models.LibraryEntity;
import devOps.models.PublicationEntity;
import devOps.models.ReactionEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.CommentRepository;
import devOps.repositories.LibraryRepository;
import devOps.repositories.PublicationRepository;
import devOps.repositories.ReactionRepository;
import devOps.repositories.UtilisateurRepository;

@Service
public class FeedService {

    @Autowired private PublicationRepository postRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private UtilisateurRepository userRepository;
    @Autowired private LibraryRepository bibliothequeRepository;
    @Autowired private ReactionRepository reactionRepository;

    // ─── Posts ────────────────────────────────────────────────────────────────

    public List<PublicationDTO> getAllPosts(Long currentUserId) {
        return postRepository.findAllByOrderByDateCreationDesc().stream()
                .map(p -> convertPostToDTO(p, currentUserId))
                .collect(Collectors.toList());
    }

    public PublicationDTO getPostById(Long id) {
        PublicationEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post non trouvé"));
        return convertPostToDTO(post, null);
    }

    @Transactional
    public PublicationDTO createPost(PublicationDTO publicationDTO, Long userId) {
        UtilisateurEntity auteur = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        PublicationEntity post = new PublicationEntity();
        post.setContenu(publicationDTO.getContenu());
        post.setAuteur(auteur);
        post.setEstRepost(false);

        if (publicationDTO.getBibliothequeId() != null) {
            LibraryEntity bibliotheque = bibliothequeRepository
                    .findById(publicationDTO.getBibliothequeId())
                    .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));
            post.setLibraryEntity(bibliotheque);
        }

        return convertPostToDTO(postRepository.save(post), userId);
    }

    // ─── Repost ───────────────────────────────────────────────────────────────

    @Transactional
    public PublicationDTO repost(Long postOriginalId, String commentaire, Long userId) {
        UtilisateurEntity auteur = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        PublicationEntity postOriginal = postRepository.findById(postOriginalId)
                .orElseThrow(() -> new RuntimeException("Post original non trouvé"));

        // Si le post est déjà un repost, on reposte le post original
        PublicationEntity source = postOriginal.getEstRepost() != null && postOriginal.getEstRepost()
                ? postOriginal.getPostOriginal()
                : postOriginal;

        PublicationEntity repost = new PublicationEntity();
        repost.setContenu(commentaire != null && !commentaire.isBlank() ? commentaire : "");
        repost.setAuteur(auteur);
        repost.setPostOriginal(source);
        repost.setEstRepost(true);

        return convertPostToDTO(postRepository.save(repost), userId);
    }

    // ─── Réactions ────────────────────────────────────────────────────────────

    @Transactional
    public ReactionDTO reagir(Long postId, ReactionType type, Long userId) {
        PublicationEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post non trouvé"));
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Optional<ReactionEntity> existante = reactionRepository.findByUserIdAndPostId(userId, postId);

        if (existante.isPresent()) {
            if (existante.get().getType() == type) {
                // Même réaction → on la retire (toggle)
                reactionRepository.delete(existante.get());
                return buildReactionDTO(postId, null);
            } else {
                // Réaction différente → on la change
                existante.get().setType(type);
                reactionRepository.save(existante.get());
                return buildReactionDTO(postId, type);
            }
        } else {
            // Nouvelle réaction
            ReactionEntity reaction = new ReactionEntity();
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setType(type);
            reactionRepository.save(reaction);
            return buildReactionDTO(postId, type);
        }
    }

    private ReactionDTO buildReactionDTO(Long postId, ReactionType maReaction) {
        List<Object[]> counts = reactionRepository.countByTypeForPost(postId);
        Map<String, Long> comptages = new LinkedHashMap<>();
        for (ReactionType t : ReactionType.values()) comptages.put(t.name(), 0L);
        for (Object[] row : counts) comptages.put(((ReactionType) row[0]).name(), (Long) row[1]);

        ReactionDTO dto = new ReactionDTO();
        dto.setPostId(postId);
        dto.setComptages(comptages);
        dto.setMaReaction(maReaction != null ? maReaction.name() : null);
        return dto;
    }

    // ─── Commentaires ─────────────────────────────────────────────────────────

    @Transactional
    public CommentDTO addCommentToPost(Long postId, CommentDTO commentDTO, Long userId) {
        PublicationEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post non trouvé"));
        UtilisateurEntity auteur = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        CommentEntity comment = new CommentEntity();
        comment.setContenu(commentDTO.getContenu());
        comment.setAuteur(auteur);
        comment.setPost(post);

        return convertCommentToDTO(commentRepository.save(comment));
    }

    @Transactional
    public CommentDTO addReplyToComment(Long commentId, CommentDTO replyDTO, Long userId) {
        CommentEntity parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));
        UtilisateurEntity auteur = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        CommentEntity reply = new CommentEntity();
        reply.setContenu(replyDTO.getContenu());
        reply.setAuteur(auteur);
        reply.setPost(parentComment.getPost());
        reply.setParentComment(parentComment);

        return convertCommentToDTO(commentRepository.save(reply));
    }

    public List<CommentDTO> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByDateCreationAsc(postId).stream()
                .filter(c -> c.getParentComment() == null)
                .map(this::convertCommentToDTO)
                .collect(Collectors.toList());
    }

    // ─── Conversion ───────────────────────────────────────────────────────────

    private PublicationDTO convertPostToDTO(PublicationEntity post, Long currentUserId) {
        PublicationDTO dto = new PublicationDTO();
        dto.setId(post.getId());
        dto.setContenu(post.getContenu());
        dto.setAuteurId(post.getAuteur().getId());
        dto.setAuteurUsername(post.getAuteur().getUsername());
        dto.setDateCreation(post.getDateCreation());
        dto.setEstRepost(post.getEstRepost());

        if (post.getLibraryEntity() != null) {
            dto.setBibliothequeId(post.getLibraryEntity().getId());
            dto.setBibliothequeNom(post.getLibraryEntity().getNom());
        }

        // Repost : infos du post original
        if (Boolean.TRUE.equals(post.getEstRepost()) && post.getPostOriginal() != null) {
            PublicationEntity original = post.getPostOriginal();
            dto.setPostOriginalId(original.getId());
            dto.setPostOriginalAuteur(original.getAuteur().getUsername());
            dto.setPostOriginalContenu(original.getContenu());
            dto.setPostOriginalDate(original.getDateCreation());
        }

        // Nombre de reposts de ce post
        long nbReposts = postRepository.findAllByOrderByDateCreationDesc().stream()
                .filter(p -> Boolean.TRUE.equals(p.getEstRepost())
                        && p.getPostOriginal() != null
                        && p.getPostOriginal().getId().equals(post.getId()))
                .count();
        dto.setNbReposts(nbReposts);

        // Réactions
        List<Object[]> counts = reactionRepository.countByTypeForPost(post.getId());
        Map<String, Long> comptages = new LinkedHashMap<>();
        for (ReactionType t : ReactionType.values()) comptages.put(t.name(), 0L);
        for (Object[] row : counts) comptages.put(((ReactionType) row[0]).name(), (Long) row[1]);
        dto.setReactions(comptages);

        // Ma réaction
        if (currentUserId != null) {
            reactionRepository.findByUserIdAndPostId(currentUserId, post.getId())
                    .ifPresent(r -> dto.setMaReaction(r.getType().name()));
        }

        // Commentaires (premier niveau uniquement)
        List<CommentDTO> comments = post.getComments().stream()
                .filter(c -> c.getParentComment() == null)
                .map(this::convertCommentToDTO)
                .collect(Collectors.toList());
        dto.setComments(comments);

        return dto;
    }

    private CommentDTO convertCommentToDTO(CommentEntity comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContenu(comment.getContenu());
        dto.setAuteurId(comment.getAuteur().getId());
        dto.setAuteurUsername(comment.getAuteur().getUsername());
        dto.setPostId(comment.getPost().getId());
        dto.setDateCreation(comment.getDateCreation());
        if (comment.getParentComment() != null) dto.setParentCommentId(comment.getParentComment().getId());
        dto.setReponses(comment.getReponses().stream().map(this::convertCommentToDTO).collect(Collectors.toList()));
        return dto;
    }
}