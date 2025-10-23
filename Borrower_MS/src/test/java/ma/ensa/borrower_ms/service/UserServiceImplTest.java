/*
package ma.ensa.borrower_ms.service;

import ma.ensa.borrower_ms.dto.*;
import ma.ensa.borrower_ms.entity.Role;
import ma.ensa.borrower_ms.entity.User;
import ma.ensa.borrower_ms.exception.CredentialsInvalidException;
import ma.ensa.borrower_ms.exception.DuplicateUserException;
import ma.ensa.borrower_ms.exception.UserNotFoundException;
import ma.ensa.borrower_ms.mapper.UserMapper;
import ma.ensa.borrower_ms.repository.UserRepository;
import ma.ensa.borrower_ms.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("yassine")
                .email("yassine@gmail.com")
                .password("encodedPass")
                .role(Role.USER)
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .username("yassine")
                .email("yassine@gmail.com")
                .role("USER")
                .build();
    }

    // ---------- registerUser ----------
    @Test
    void registerUser_ShouldRegisterSuccessfully() {
        UserRegisterDTO dto = UserRegisterDTO.builder()
                .username("yassine")
                .email("yassine@gmail.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals("yassine", result.getUsername());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void registerUser_ShouldThrowDuplicateException_WhenUserbyEmailExists() {
        UserRegisterDTO dto = UserRegisterDTO.builder()
                .username("yassine")
                .email("yassine@gmail.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.registerUser(dto));
    }

    @Test
    void registerUser_ShouldThrowDuplicateException_WhenUserbyUsernameExists() {
        UserRegisterDTO dto = UserRegisterDTO.builder()
                .username("yassine")
                .email("yassine@gmail.com")
                .password("password123")
                .build();

        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.registerUser(dto));
    }

    // ---------- loginUser ----------
    @Test
    void loginUser_ShouldReturnToken_WhenCredentialsValid() {
        UserLoginDTO dto = UserLoginDTO.builder()
                .email("yassine@gmail.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.getRole().toString())).thenReturn("jwt-token");

        UserLoginResponseDTO response = userService.loginUser(dto);

        assertNotNull(response);
        assertEquals("yassine", response.getUsername());
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginUser_ShouldThrow_WhenPasswordInvalid() {
        UserLoginDTO dto = UserLoginDTO.builder()
                .email("yassine@gmail.com")
                .password("wrong")
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(CredentialsInvalidException.class, () -> userService.loginUser(dto));
    }

    @Test
    void loginUser_ShouldThrow_WhenUserNotFound() {
        UserLoginDTO dto = UserLoginDTO.builder()
                .email("unknown@gmail.com")
                .password("password")
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.loginUser(dto));
    }

    // ---------- getUserById ----------
    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.getUserById(1L);

        assertEquals("yassine", result.getUsername());
    }

    @Test
    void getUserById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2L));
    }

    // ---------- updateUser ----------
    @Test
    void updateUser_ShouldUpdateUsernameAndPassword_WhenValid() {
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .username("newName")
                .password("newPass")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(updateDTO.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(updateDTO.getPassword())).thenReturn("encodedNewPass");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        assertNotNull(result);
        verify(userRepository).save(any());
        verify(passwordEncoder).encode("newPass");
    }


    @Test
    void updateUser_ShouldThrow_WhenUsernameAlreadyExists() {
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .username("existingUser")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(updateDTO.getUsername())).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.updateUser(1L, updateDTO));
        verify(userRepository, never()).save(any());
    }


    @Test
    void updateUser_ShouldNotChangeUsername_WhenSameAsExisting() {
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .username("yassine") // identical to current username
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        assertEquals("yassine", result.getUsername());
        verify(userRepository).save(any());
        verify(userRepository, never()).existsByUsername(any());
    }


    @Test
    void updateUser_ShouldUpdateOnlyPassword_WhenUsernameIsNull() {
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .password("onlyPassword")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updateDTO.getPassword())).thenReturn("encodedPassOnly");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        assertNotNull(result);
        verify(passwordEncoder).encode("onlyPassword");
        verify(userRepository).save(any());
    }


    @Test
    void updateUser_ShouldThrow_WhenUserNotFound() {
        UserUpdateDTO dto = new UserUpdateDTO();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, dto));
    }


    // ---------- deleteUser ----------
    @Test
    void deleteUser_ShouldDeleteSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.existsById(2L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(2L));
    }
}


 */