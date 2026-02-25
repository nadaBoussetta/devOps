package devOps.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenUsernameIsBlank() {
        AuthDTO dto = new AuthDTO("", "test@example.com", "password123");

        Set<ConstraintViolation<AuthDTO>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("obligatoire");
    }

    @Test
    void shouldFailWhenEmailIsInvalidOrBlank() {
        AuthDTO dto1 = new AuthDTO("john", "", "pass");
        AuthDTO dto2 = new AuthDTO("john", "invalid-email", "pass");

        assertThat(validator.validate(dto1)).isNotEmpty();
        assertThat(validator.validate(dto2)).isNotEmpty();
    }

    @Test
    void shouldPassWhenAllFieldsValid() {
        AuthDTO dto = new AuthDTO("john.doe", "john.doe@company.com", "StrongPass123!");

        Set<ConstraintViolation<AuthDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}