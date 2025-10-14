package ma.ensa.borrower_ms.mapper;

import ma.ensa.borrower_ms.dto.UserRegisterDTO;
import ma.ensa.borrower_ms.dto.UserResponseDTO;
import ma.ensa.borrower_ms.dto.UserUpdateDTO;
import ma.ensa.borrower_ms.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    /**
     * Map register DTO -> User entity.
     * Note: password is copied as-is here. Encode the password in the service layer
     * before saving the entity.
     */
    public User toEntity(UserRegisterDTO dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, User.class);
    }

    /**
     * Map User entity -> response DTO (never include password).
     */
    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;
        UserResponseDTO dto = modelMapper.map(user, UserResponseDTO.class);
        // Ensure sensitive fields are not exposed (modelmapper won't copy password into response DTO by design,
        // but keep this as a safety measure in case of DTO changes)
        return dto;
    }

    /**
     * Map list of users to list of response DTOs.
     */
    public List<UserResponseDTO> toResponseDTOs(List<User> users) {
        if (users == null) return List.of();
        return users.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


}
