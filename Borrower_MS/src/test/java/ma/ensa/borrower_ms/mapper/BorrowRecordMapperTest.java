/*
package ma.ensa.borrower_ms.mapper;


import ma.ensa.borrower_ms.dto.BorrowRecordResponseDTO;
import ma.ensa.borrower_ms.entity.BorrowRecord;
import ma.ensa.borrower_ms.entity.Role;
import ma.ensa.borrower_ms.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
public class BorrowRecordMapperTest {

    private BorrowRecordMapper mapper ;

    @BeforeEach
    void setup(){
        mapper = new BorrowRecordMapper();
    }



    @Test
    void testToResponseDTO(){
        BorrowRecord entity = new BorrowRecord();
        entity.setId(1L);
        entity.setBookId(1L);
        entity.setBorrowDate(LocalDate.now());
        entity.setReturnDate(LocalDate.now());
        User borrower = new User(1L,"yassin99e","yben@gmail.com","password123", Role.USER);
        entity.setBorrower(borrower);


        BorrowRecordResponseDTO borrowRecordResponseDTO = mapper.toResponseDTO(entity);

        assertNotNull(borrowRecordResponseDTO);
        assertEquals(entity.getId(), borrowRecordResponseDTO.getId());
        assertEquals(entity.getBookId(), borrowRecordResponseDTO.getBookId());
        assertEquals(entity.getBorrower().getId(), borrowRecordResponseDTO.getBorrowerId());
        assertEquals(entity.getBorrowDate(), borrowRecordResponseDTO.getBorrowDate());

    }


}


 */