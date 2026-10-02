package m.ilyina.find_trining.exception;

/**
 * Thrown when a requested entity does not exist.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException of(String entityName, Object id) {
        return new NotFoundException(entityName + " с id=" + id + " не найден");
    }
}
