package ma.ensa.borrower_ms.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException() {
        super("User   already exists.");
    }



}
