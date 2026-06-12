package devOps.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devOps.dtos.CommentDTO;
import devOps.dtos.PublicationDTO;
import devOps.dtos.ReactionDTO;
import devOps.enums.ReactionType;
import devOps.services.FeedService;
import devOps.util.SecurityUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @GetMapping
    public ResponseEntity<List<PublicationDTO>> getAllPosts() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(feedService.getAllPosts(currentUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(feedService.getPostById(id));
    }

    @PostMapping
    public ResponseEntity<PublicationDTO> createPost(
            @Valid @RequestBody PublicationDTO postDTO,
            Authentication authentication) {
        Long userId = extractUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(feedService.createPost(postDTO, userId));
    }

    // ✅ Repost : POST /api/feed/{postId}/repost
    @PostMapping("/{postId}/repost")
    public ResponseEntity<PublicationDTO> repost(
            @PathVariable Long postId,
            @RequestBody(required = false) Map<String, String> body) {
        Long userId = extractUserId();
        String commentaire = body != null ? body.get("commentaire") : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(feedService.repost(postId, commentaire, userId));
    }

    // ✅ Réaction : POST /api/feed/{postId}/reactions  body: { "type": "JAIME" }
    @PostMapping("/{postId}/reactions")
    public ResponseEntity<ReactionDTO> reagir(
            @PathVariable Long postId,
            @RequestBody Map<String, String> body) {
        Long userId = extractUserId();
        ReactionType type = ReactionType.valueOf(body.get("type").toUpperCase());
        return ResponseEntity.ok(feedService.reagir(postId, type, userId));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDTO> addCommentToPost(
            @PathVariable Long postId,
            @Valid @RequestBody CommentDTO commentDTO,
            Authentication authentication) {
        Long userId = extractUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(feedService.addCommentToPost(postId, commentDTO, userId));
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentDTO> addReplyToComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDTO replyDTO,
            Authentication authentication) {
        Long userId = extractUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(feedService.addReplyToComment(commentId, replyDTO, userId));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(feedService.getCommentsByPost(postId));
    }

    private Long extractUserId() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new RuntimeException("Utilisateur non authentifié");
        return userId;
    }
}