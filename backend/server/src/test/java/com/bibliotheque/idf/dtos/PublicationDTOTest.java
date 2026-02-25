package devOps.dtos;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PublicationDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldBeValidWhenContenuPresent() {
        PublicationDTO dto = new PublicationDTO();
        dto.setContenu("Bonjour");

        Set<ConstraintViolation<PublicationDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenContenuBlank() {
        PublicationDTO dto = new PublicationDTO();
        dto.setContenu("");

        Set<ConstraintViolation<PublicationDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}