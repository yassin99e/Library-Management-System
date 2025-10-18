package ma.ensa.borrower_ms.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ma.ensa.borrower_ms.dto.BorrowRecordResponseDTO;
import ma.ensa.borrower_ms.dto.BorrowResponseDTO;
import ma.ensa.borrower_ms.exception.ForbiddenException;
import ma.ensa.borrower_ms.service.BorrowRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    // ✅ Borrower must match authenticated user
    @PostMapping("/{borrowerId}/{bookId}")
    public ResponseEntity<BorrowRecordResponseDTO> borrowBook(
            @PathVariable Long borrowerId,
            @PathVariable Long bookId,
            HttpServletRequest request) {

        String userId = request.getHeader("X-User-Id");
        if (userId == null || !userId.equals(String.valueOf(borrowerId))) {
            throw new ForbiddenException("Access denied: You can only borrow books for your own account.");
        }

        return ResponseEntity.ok(borrowRecordService.borrowBook(borrowerId, bookId));
    }

    // ✅ Borrower must match authenticated user
    @PutMapping("/{borrowerId}/{bookId}/return")
    public ResponseEntity<BorrowRecordResponseDTO> returnBook(
            @PathVariable Long borrowerId,
            @PathVariable Long bookId,
            HttpServletRequest request) {

        String userId = request.getHeader("X-User-Id");
        if (userId == null || !userId.equals(String.valueOf(borrowerId))) {
            throw new ForbiddenException("Access denied: You can only return your own borrowed books.");
        }

        return ResponseEntity.ok(borrowRecordService.returnBook(borrowerId, bookId));
    }


    @GetMapping("/borrowed/{borrowerId}")
    public ResponseEntity<List<BorrowResponseDTO>> CurrentlyBorrowedBooks(
            @PathVariable Long borrowerId,
            HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null || !userId.equals(String.valueOf(borrowerId))) {
            throw new ForbiddenException("Access denied: You can only return your own borrowed books.");
        }
        return ResponseEntity.ok(borrowRecordService.findCurrentlyBorrowedBooksByUser(borrowerId));

    }


    @GetMapping("/history/{borrowerId}")
    public ResponseEntity<List<BorrowResponseDTO>> ReturnedBorrowedBooks(
            @PathVariable Long borrowerId,
            HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId == null || !userId.equals(String.valueOf(borrowerId))) {
            throw new ForbiddenException("Access denied: You can only return your own borrowed books.");
        }
        return ResponseEntity.ok(borrowRecordService.findReturnedBorrowedBookIdsByUser(borrowerId));

    }

}
