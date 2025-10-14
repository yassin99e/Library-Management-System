package ma.ensa.borrower_ms.exception;

public class AlreadyBorrowedException extends RuntimeException{

    public AlreadyBorrowedException(Long borrowerId,Long bookId){
        super("Borrower " + borrowerId + " already borrowed book " + bookId + " and has not returned it yet.");
    }

}
