package m.ilyina.find_trining.exception;

/**
 * Thrown when a request is well-formed but violates a domain business rule
 * (e.g. a training slot outside business hours, or one whose time already
 * passed) - distinct from {@link ConflictException}, which is for a
 * resource that already exists/is already taken.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
