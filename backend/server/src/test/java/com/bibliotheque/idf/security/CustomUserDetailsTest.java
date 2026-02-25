package devOps.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    private CustomUserDetails userDetails;
    private final String username = "assia_dev";
    private final String password = "$2a$10$hashedpasswordexample";
    private final Long userId = 42L;
    private final Collection<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
    );

    @BeforeEach
    void setUp() {
        userDetails = new CustomUserDetails(username, password, authorities, userId);
    }

    @Test
    void constructor_shouldInitializeAllFieldsCorrectly() {
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.getAuthorities()).containsExactlyInAnyOrderElementsOf(authorities);
        assertThat(userDetails.getUserId()).isEqualTo(userId);
    }

    @Test
    void getUserId_shouldReturnTheProvidedId() {
        assertThat(userDetails.getUserId()).isEqualTo(42L);
    }

    @Test
    void defaultAccountStatus_shouldBeAllTrue() {
        // Par défaut, User considère les comptes valides si non explicitement désactivés
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void authorities_shouldBeImmutable() {
        Collection<? extends GrantedAuthority> auths = userDetails.getAuthorities();
        assertThat(auths).isInstanceOf(Set.class); // User retourne un Set immuable
        // Tentative d'ajout → devrait lancer UnsupportedOperationException
        assertThatThrownBy(() -> ((Collection<GrantedAuthority>) auths).add(new SimpleGrantedAuthority("ROLE_TEST")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void equalsAndHashCode_shouldConsiderUserFieldsAndUserId() {
        CustomUserDetails same = new CustomUserDetails(username, password, authorities, userId);
        CustomUserDetails differentId = new CustomUserDetails(username, password, authorities, 99L);
        CustomUserDetails differentName = new CustomUserDetails("other", password, authorities, userId);

        // Même username + même userId → equals
        assertThat(userDetails).isEqualTo(same);
        assertThat(userDetails.hashCode()).isEqualTo(same.hashCode());

        // Différent userId → pas equals (même si username identique)
        assertThat(userDetails).isNotEqualTo(differentId);

        // Différent username → pas equals
        assertThat(userDetails).isNotEqualTo(differentName);
    }

    @Test
    void toString_shouldIncludeUsernameAndAuthorities() {
        String str = userDetails.toString();

        assertThat(str).contains(username);
        assertThat(str).contains("ROLE_USER");
        assertThat(str).contains("ROLE_ADMIN");
        // Le userId n'est pas forcément dans toString() par défaut (dépend de User),
        // mais on vérifie au moins les champs hérités
        assertThat(str).doesNotContain("password"); // mot de passe masqué par sécurité
    }

    @Test
    void canBeUsedAsRegularUserInSecurityContext() {
        // Vérifie la compatibilité polymorphique
        User casted = userDetails; // doit compiler et fonctionner
        assertThat(casted.getUsername()).isEqualTo(username);
        assertThat(casted.getAuthorities()).hasSize(2);
    }
}