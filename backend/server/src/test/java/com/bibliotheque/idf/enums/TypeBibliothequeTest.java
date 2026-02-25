package devOps.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeBibliothequeTest {

    @Test
    void shouldContainTwoValues() {
        TypeBibliotheque[] values = TypeBibliotheque.values();

        assertEquals(2, values.length);
    }

    @Test
    void shouldContainExpectedValues() {
        assertEquals(TypeBibliotheque.PUBLIQUE, TypeBibliotheque.valueOf("PUBLIQUE"));
        assertEquals(TypeBibliotheque.UNIVERSITAIRE, TypeBibliotheque.valueOf("UNIVERSITAIRE"));
    }
}