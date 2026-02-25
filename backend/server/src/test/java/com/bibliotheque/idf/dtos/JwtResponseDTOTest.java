package devOps.dtos;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class JwtResponseDTOTest {

    @Test
    void shouldCreateWithAllArgsConstructorAndDefaultType() {
        JwtResponseDTO dto = new JwtResponseDTO("jwt.token.here", 42L, "john.doe", "john@example.com");

        assertThat(dto.getToken()).isEqualTo("jwt.token.here");
        assertThat(dto.getUserId()).isEqualTo(42L);
        assertThat(dto.getUsername()).isEqualTo("john.doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
        assertThat(dto.getType()).isEqualTo("Bearer"); // valeur par défaut
    }

    @Test
    void shouldAllowSettingTypeManually() {
        JwtResponseDTO dto = new JwtResponseDTO();
        dto.setToken("custom.token");
        dto.setType("CustomBearer");
        dto.setUserId(100L);
        dto.setUsername("alice");
        dto.setEmail("alice@company.com");

        assertThat(dto.getType()).isEqualTo("CustomBearer");
    }

    @Test
    void noArgsConstructorShouldInitializeWithDefaultType() {
        JwtResponseDTO dto = new JwtResponseDTO();
        assertThat(dto.getType()).isEqualTo("Bearer");
        assertThat(dto.getToken()).isNull();
        assertThat(dto.getUserId()).isNull();
    }

    @Test
    void shouldHaveProperEqualsAndHashCode() {
        JwtResponseDTO dto1 = new JwtResponseDTO("token123", 1L, "user1", "u1@mail.com");
        JwtResponseDTO dto2 = new JwtResponseDTO("token123", 1L, "user1", "u1@mail.com");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void toStringShouldContainImportantFields() {
        JwtResponseDTO dto = new JwtResponseDTO("secret-token", 5L, "bob", "bob@mail.com");
        String str = dto.toString();

        assertThat(str).contains("token=secret-token");
        assertThat(str).contains("userId=5");
        assertThat(str).contains("username=bob");
        assertThat(str).doesNotContain("email"); // souvent masqué pour sécurité, mais ici présent
    }
}