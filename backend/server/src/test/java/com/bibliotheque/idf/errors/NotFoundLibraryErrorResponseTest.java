package devOps.errors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundLibraryErrorResponseTest {

    @Test
    void shouldCreateObjectWithMessage() {
        String message = "Library not found";

        NotFoundLibraryErrorResponse error =
                new NotFoundLibraryErrorResponse(message);

        assertEquals(message, error.getMessage());
    }

    @Test
    void shouldAllowSettingMessage() {
        NotFoundLibraryErrorResponse error =
                new NotFoundLibraryErrorResponse("initial");

        error.setMessage("updated");

        assertEquals("updated", error.getMessage());
    }

    @Test
    void toStringShouldNotBeNull() {
        NotFoundLibraryErrorResponse error =
                new NotFoundLibraryErrorResponse("error");

        assertNotNull(error.toString());
    }
}