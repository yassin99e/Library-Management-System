package ma.ensa.book_ms.Exceptions;


public class NegativeCopiesException extends RuntimeException {
    public NegativeCopiesException(Long id) {
        super("Book with ID " + id + " cannot have negative copies.");
    }
}