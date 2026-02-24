package devOps.controllers;

import devOps.dtos.CommentDTO;
import devOps.dtos.PublicationDTO;
import devOps.services.FeedService;
import devOps.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @GetMapping
    public ResponseEntity<List<PublicationDTO>> getAllPosts() {
        List<PublicationDTO> posts = feedService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationDTO> getPostById(@PathVariable Long id) {
        PublicationDTO post = feedService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PublicationDTO> createPost(
            @Valid @RequestBody PublicationDTO postDTO,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        PublicationDTO createdPost = feedService.createPost(postDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDTO> addCommentToPost(
            @PathVariable Long postId,
            @Valid @RequestBody CommentDTO commentDTO,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        CommentDTO comment = feedService.addCommentToPost(postId, commentDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentDTO> addReplyToComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDTO replyDTO,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        CommentDTO reply = feedService.addReplyToComment(commentId, replyDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentDTO> comments = feedService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    private Long extractUserId(Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        return userId;
    }
}