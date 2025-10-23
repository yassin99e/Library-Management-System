/*
package ma.ensa.borrower_ms.mapper;


import ma.ensa.borrower_ms.dto.BookResponseDTO;
import ma.ensa.borrower_ms.dto.UserRegisterDTO;
import ma.ensa.borrower_ms.dto.UserResponseDTO;
import ma.ensa.borrower_ms.entity.Role;
import ma.ensa.borrower_ms.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class UserMapperTest {

    private UserMapper userMapper;


    @BeforeEach
    void setup() {
        this.userMapper = new UserMapper();
    }

    // let's test the function toEntity :
    @Test
    void testToEntity_success(){
        UserRegisterDTO dto = new UserRegisterDTO();

        dto.setUsername("yassin99e");
        dto.setEmail("yassine.benakki@gmail.com");
        dto.setPassword("password123");

        User user = userMapper.toEntity(dto);
        assertNotNull(user);
        assertEquals(user.getUsername(),dto.getUsername());
        assertEquals(user.getEmail(),dto.getEmail());
        assertEquals(user.getPassword(),dto.getPassword());

    }

    @Test
    void testToEntity_null(){
        UserRegisterDTO dto = null;

        User user = userMapper.toEntity(dto);
        assertNull(user);

    }

    // let's test to ResponseDTO :
    @Test
    void testtoResponseDTO(){
        User user = User.builder()
                .id(1L)
                .username("yassin99e")
                .email("yassine.benakki@gmail.com")
                .role(Role.USER)
                .password("password123")
                .build();

        UserResponseDTO responseDTO = userMapper.toResponseDTO(user);
        assertNotNull(responseDTO);
        assertEquals(responseDTO.getId(),user.getId());
        assertEquals(responseDTO.getUsername(),user.getUsername());
        assertEquals(responseDTO.getEmail(),user.getEmail());
    }

    @Test
    void testtoResponseDTO_null(){
        User user = null;

        UserResponseDTO responseDTO = userMapper.toResponseDTO(user);
        assertNull(responseDTO);

    }


    @Test
    void testToResponseDTOList() {
        // 👤 Normal User
        User user = User.builder()
                .username("yassin99e")
                .email("yassine.benakki@gmail.com")
                .role(Role.USER)
                .password("password123")
                .build();
        // 🛡️ Admin User
        User admin = User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .role(Role.ADMIN)
                .password("admin123")
                .build();

        List<User> users = Arrays.asList(user,admin);
        List<UserResponseDTO> responseDTOs = userMapper.toResponseDTOs(users);

        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.size());
        assertEquals(user.getUsername(),responseDTOs.get(0).getUsername());
        assertEquals(admin.getUsername(),responseDTOs.get(1).getUsername());

    }




}
*/
