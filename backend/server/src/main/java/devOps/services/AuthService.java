package devOps.services;

import devOps.dtos.AuthDTO;
import devOps.dtos.JwtResponseDTO;
import devOps.dtos.LoginDTO;
import devOps.models.UtilisateurEntity;
import devOps.repositories.UtilisateurRepository;
import devOps.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public UtilisateurEntity register(AuthDTO authDTO) {
        if (userRepository.existsByUsername(authDTO.getUsername())) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà utilisé");
        }

        if (userRepository.existsByEmail(authDTO.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        UtilisateurEntity user = new UtilisateurEntity();
        user.setUsername(authDTO.getUsername());
        user.setEmail(authDTO.getEmail());
        user.setPassword(passwordEncoder.encode(authDTO.getPassword()));
        user.getRoles().add("ROLE_USER");

        return userRepository.save(user);
    }

    public JwtResponseDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        UtilisateurEntity user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return new JwtResponseDTO(token, user.getId(), user.getUsername(), user.getEmail());
    }
}