package ma.ensa.book_ms.Exceptions;

public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String title) {
        super("Book with title '" + title + "' already exists.");
    }
}