package devOps.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeNotificationTest {

    @Test
    void shouldContainAllNotificationTypes() {
        assertEquals(5, TypeNotification.values().length);
    }

    @Test
    void shouldReturnCorrectDescription() {
        assertEquals("Fermeture imminente",
                TypeNotification.FERMETURE_BIBLIOTHEQUE.getDescription());

        assertEquals("Recommandation personnalisée",
                TypeNotification.RECOMMANDATION.getDescription());
    }

    @Test
    void valueOfShouldWork() {
        TypeNotification type = TypeNotification.valueOf("LIVRE_DISPONIBLE");

        assertEquals(TypeNotification.LIVRE_DISPONIBLE, type);
    }
}