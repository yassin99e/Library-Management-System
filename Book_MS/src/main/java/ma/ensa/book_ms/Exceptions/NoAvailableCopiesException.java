package ma.ensa.book_ms.Exceptions;


public class NoAvailableCopiesException extends RuntimeException {
    public NoAvailableCopiesException(Long id) {
        super("No available copies left for book with ID " + id + ".");
    }
}