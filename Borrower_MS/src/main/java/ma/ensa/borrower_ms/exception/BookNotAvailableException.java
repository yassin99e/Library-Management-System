package ma.ensa.borrower_ms.exception;

public class BookNotAvailableException extends RuntimeException{

    public BookNotAvailableException(Long id) {
        super("Book " + id + " has no available copies.");
    }

}
