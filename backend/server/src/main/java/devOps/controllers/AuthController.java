package devOps.controllers;

import devOps.dtos.AuthDTO;
import devOps.dtos.JwtResponseDTO;
import devOps.dtos.LoginDTO;
import devOps.models.UtilisateurEntity;
import devOps.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDTO authDTO) {
        try {
            UtilisateurEntity user = authService.register(authDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur créé avec succès");
            response.put("username", user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            JwtResponseDTO response = authService.login(loginDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Identifiants invalides");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}
