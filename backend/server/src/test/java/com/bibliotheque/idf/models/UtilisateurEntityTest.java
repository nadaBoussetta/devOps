package devOps.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilisateurEntityTest {

    @Test
    void constructorShouldSetFieldsAndDefaultRole() {
        UtilisateurEntity user = new UtilisateurEntity(
                "assia",
                "assia@example.com",
                "password"
        );

        assertEquals("assia", user.getUsername());
        assertEquals("assia@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.getRoles().contains("ROLE_USER"));
    }

    @Test
    void collectionsShouldBeInitialized() {
        UtilisateurEntity user = new UtilisateurEntity();

        assertNotNull(user.getRoles());
        assertNotNull(user.getComments());
        assertNotNull(user.getNotations());
        assertNotNull(user.getFavoris());
    }
}