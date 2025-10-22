package ma.ensa.notification_ms.Exceptions;

public class UserserviceDownException extends RuntimeException{

    public UserserviceDownException(){
        super("User service not available");
    }

}
