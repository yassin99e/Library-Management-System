package ma.ensa.borrower_ms.service;

import ma.ensa.borrower_ms.dto.*;

import java.util.List;

public interface UserService {

    // Auth functions :
    UserResponseDTO registerUser(UserRegisterDTO request);
    UserLoginResponseDTO loginUser(UserLoginDTO request);


    // Crud functions :
    UserResponseDTO getUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO updateUser(Long id, UserUpdateDTO request);
    void deleteUser(Long id);
}
