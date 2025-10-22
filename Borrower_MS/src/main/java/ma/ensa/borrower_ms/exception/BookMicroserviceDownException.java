package ma.ensa.borrower_ms.exception;

public class BookMicroserviceDownException extends RuntimeException{

    public BookMicroserviceDownException(){
        super("Book Microservice not available");
    }

}
