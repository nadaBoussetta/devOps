package devOps.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeLieuTest {

    @Test
    void shouldContainAllExpectedValues() {
        assertEquals(TypeLieu.BIBLIOTHEQUE_UNIVERSITAIRE,
                TypeLieu.valueOf("BIBLIOTHEQUE_UNIVERSITAIRE"));

        assertEquals(TypeLieu.MEDIATHEQUE,
                TypeLieu.valueOf("MEDIATHEQUE"));

        assertEquals(5, TypeLieu.values().length);
    }
}