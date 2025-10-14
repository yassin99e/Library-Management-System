package ma.ensa.borrower_ms.mapper;

import ma.ensa.borrower_ms.dto.BorrowRecordResponseDTO;
import ma.ensa.borrower_ms.entity.BorrowRecord;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BorrowRecordMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public BorrowRecordResponseDTO toResponseDTO(BorrowRecord entity) {
        return modelMapper.map(entity, BorrowRecordResponseDTO.class);
    }
}
