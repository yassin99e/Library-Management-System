package ma.ensa.book_ms.Mapper;



import ma.ensa.book_ms.DTO.BookRequestDTO;
import ma.ensa.book_ms.DTO.BookResponseDTO;
import ma.ensa.book_ms.Entity.Book;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public Book toEntity(BookRequestDTO dto) {
        return modelMapper.map(dto, Book.class);
    }

    public BookResponseDTO toResponseDTO(Book entity) {
        return modelMapper.map(entity, BookResponseDTO.class);
    }

    public List<BookResponseDTO> toResponseDTOList(List<Book> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
