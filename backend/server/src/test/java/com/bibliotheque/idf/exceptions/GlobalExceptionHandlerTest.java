package devOps.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationExceptions_shouldReturnBadRequestWithFieldErrors() {
        // Arrange : on simule une validation qui échoue sur un champ "username"
        Object target = new Object();
        BindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "username", "Le nom d'utilisateur est obligatoire"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("username"));
        assertEquals("Le nom d'utilisateur est obligatoire", response.getBody().get("username"));
    }

    @Test
    void handleRuntimeException_shouldReturnBadRequestWithErrorMessage() {
        // Arrange
        RuntimeException ex = new RuntimeException("Une erreur côté client");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleRuntimeException(ex);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Une erreur côté client", response.getBody().get("error"));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerErrorWithDetails() {
        // Arrange
        Exception ex = new Exception("Détail technique de l'erreur");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleGenericException(ex);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        Map<String, String> body = response.getBody();

        assertEquals("Une erreur interne s'est produite", body.get("error"));
        assertEquals("Détail technique de l'erreur", body.get("details"));
    }
}