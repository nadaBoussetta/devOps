package devOps.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SessionControllerTest {

    @Test
    void classShouldLoad() {
        assertDoesNotThrow(() -> Class.forName("devOps.controllers.SessionController"));
    }
}