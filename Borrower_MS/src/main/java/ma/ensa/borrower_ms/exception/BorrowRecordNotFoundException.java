package ma.ensa.borrower_ms.exception;

public class BorrowRecordNotFoundException extends RuntimeException {
    public BorrowRecordNotFoundException(Long borrowerId,Long bookId) {
        super("No active borrow record found for borrower " + borrowerId + " and book " + bookId);

    }
}
