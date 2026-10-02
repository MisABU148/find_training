package m.ilyina.find_trining.exception;

/**
 * Thrown when a request would violate a uniqueness/business constraint
 * (e.g. an email or name that must be unique is already taken).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
