package devOps.dtos;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SessionDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldBeValidWhenCorrect() {
        SessionDTO dto = new SessionDTO();
        dto.setObjectif("Réviser Java");
        dto.setDureeMinutes(60);

        Set<ConstraintViolation<SessionDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDureeNegative() {
        SessionDTO dto = new SessionDTO();
        dto.setObjectif("Réviser Java");
        dto.setDureeMinutes(-10);

        Set<ConstraintViolation<SessionDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}