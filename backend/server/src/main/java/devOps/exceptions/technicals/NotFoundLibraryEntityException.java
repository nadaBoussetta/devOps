package devOps.exceptions.technicals;

public class NotFoundLibraryEntityException extends RuntimeException {
    public NotFoundLibraryEntityException(String message) {
        super(message);
    }
}
