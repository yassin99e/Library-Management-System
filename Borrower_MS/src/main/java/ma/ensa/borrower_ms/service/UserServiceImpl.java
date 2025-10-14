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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private UserMapper userMapper;

    private PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    UserServiceImpl(JwtUtil jwtUtil,PasswordEncoder passwordEncoder,UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public UserResponseDTO registerUser(UserRegisterDTO request) {

        if(userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUserException();
        }

        User user = userMapper.toEntity(request);
        user.setRole(Role.USER);

        // Hash the password before saving it
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User saved = userRepository.save(user);

        return userMapper.toResponseDTO(saved);
    }

    @Override
    public UserLoginResponseDTO loginUser(UserLoginDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException());

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CredentialsInvalidException();
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole().toString());

        UserLoginResponseDTO response = UserLoginResponseDTO.builder()
                .username(user.getUsername())
                .token(token)
                .build();



        return response;
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException());
        return userMapper.toResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userMapper.toResponseDTOs(userRepository.findAll());
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException());

        if(request.getUsername() != null) {
            if(!user.getUsername().equals(request.getUsername())) {
                if(userRepository.existsByUsername(request.getUsername())) {
                    throw new DuplicateUserException();
                }
                else{
                    user.setUsername(request.getUsername());
                }
            }
        }
        if(request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);

    }
}
