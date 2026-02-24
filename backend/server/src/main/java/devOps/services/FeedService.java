package devOps.services;

import devOps.dtos.CommentDTO;
import devOps.dtos.PublicationDTO;
import devOps.models.LibraryEntity;
import devOps.models.CommentEntity;
import devOps.models.PublicationEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.LibraryRepository;
import devOps.repositories.CommentRepository;
import devOps.repositories.PublicationRepository;
import devOps.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedService {

    @Autowired
    private PublicationRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private LibraryRepository bibliothequeRepository;

    public List<PublicationDTO> getAllPosts() {
        return postRepository.findAllByOrderByDateCreationDesc().stream()
                .map(this::convertPostToDTO)
                .collect(Collectors.toList());
    }


    public PublicationDTO getPostById(Long id) {
        PublicationEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post non trouvé"));
        return convertPostToDTO(post);
    }

    @Transactional
    public PublicationDTO createPost(PublicationDTO publicationDTO, Long userId) {

        UtilisateurEntity auteur = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        PublicationEntity post = new PublicationEntity();
        post.setContenu(publicationDTO.getContenu());
        post.setAuteur(auteur);

        if (publicationDTO.getBibliothequeId() != null) {
            LibraryEntity bibliotheque = bibliothequeRepository
                    .findById(publicationDTO.getBibliothequeId())
                    .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));

            post.setLibraryEntity(bibliotheque);
        }

        PublicationEntity savedPost = postRepository.save(post);
        return convertPostToDTO(savedPost);
    }

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

        CommentEntity savedComment = commentRepository.save(comment);
        return convertCommentToDTO(savedComment);
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

        CommentEntity savedReply = commentRepository.save(reply);
        return convertCommentToDTO(savedReply);
    }

    public List<CommentDTO> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByDateCreationAsc(postId).stream()
                .filter(comment -> comment.getParentComment() == null) // Seulement les commentaires de premier niveau
                .map(this::convertCommentToDTO)
                .collect(Collectors.toList());
    }

    private PublicationDTO convertPostToDTO(PublicationEntity post) {
        PublicationDTO dto = new PublicationDTO();
        dto.setId(post.getId());
        dto.setContenu(post.getContenu());
        dto.setAuteurId(post.getAuteur().getId());
        dto.setAuteurUsername(post.getAuteur().getUsername());
        dto.setDateCreation(post.getDateCreation());

        if (post.getLibraryEntity() != null) {
            dto.setBibliothequeId(post.getLibraryEntity().getId());
            dto.setBibliothequeNom(post.getLibraryEntity().getNom());
        }

        List<CommentDTO> comments = post.getComments().stream()
                .filter(comment -> comment.getParentComment() == null)
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

        if (comment.getParentComment() != null) {
            dto.setParentCommentId(comment.getParentComment().getId());
        }

        List<CommentDTO> reponses = comment.getReponses().stream()
                .map(this::convertCommentToDTO)
                .collect(Collectors.toList());
        dto.setReponses(reponses);

        return dto;
    }
}