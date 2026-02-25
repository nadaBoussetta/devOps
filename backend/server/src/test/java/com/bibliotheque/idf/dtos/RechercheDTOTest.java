package devOps.dtos;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RechercheDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldBeValidWhenAllFieldsCorrect() {
        RechercheDTO dto = new RechercheDTO(
                "Paris",
                "08:00",
                "18:00",
                5.0
        );

        Set<ConstraintViolation<RechercheDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenRayonNegative() {
        RechercheDTO dto = new RechercheDTO(
                "Paris",
                "08:00",
                "18:00",
                -1.0
        );

        Set<ConstraintViolation<RechercheDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}