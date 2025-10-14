package ma.ensa.borrower_ms.exception;

public class CredentialsInvalidException extends RuntimeException{
    public CredentialsInvalidException(){
        super("Password Incorrect");
    }
}

