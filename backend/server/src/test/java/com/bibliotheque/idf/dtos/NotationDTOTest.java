package devOps.dtos;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NotationDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldBeValidWhenAllFieldsCorrect() {
        NotationDTO dto = new NotationDTO(
                1L,
                2L,
                4,
                "Très bien",
                LocalDate.now(),
                LocalDate.now(),
                "BU Paris"
        );

        Set<ConstraintViolation<NotationDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNoteOutOfRange() {
        NotationDTO dto = new NotationDTO();
        dto.setBibliothequeId(1L);
        dto.setNote(6);
        dto.setDateVisite(LocalDate.now());

        Set<ConstraintViolation<NotationDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}