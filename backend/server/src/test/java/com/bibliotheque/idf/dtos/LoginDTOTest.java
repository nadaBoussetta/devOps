package devOps.dtos;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldBeValidWhenFieldsAreCorrect() {
        LoginDTO dto = new LoginDTO("user", "password");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenUsernameIsBlank() {
        LoginDTO dto = new LoginDTO("", "password");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}