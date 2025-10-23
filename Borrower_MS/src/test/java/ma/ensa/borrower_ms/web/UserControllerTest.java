/*
package ma.ensa.borrower_ms.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import ma.ensa.borrower_ms.dto.*;
import ma.ensa.borrower_ms.exception.ForbiddenException;
import ma.ensa.borrower_ms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) //Je veux tester uniquement le comportement du contrôleur, pas les filtres de sécurité."     Unit test
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDTO userResponse;
    private UserLoginResponseDTO loginResponse;

    @BeforeEach
    void setUp() {
        userResponse = UserResponseDTO.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .role("USER")
                .build();

        loginResponse = UserLoginResponseDTO.builder()
                .username("john")
                .token("fake-jwt")
                .build();
    }

    // --- Register ---
    @Test
    void registerUser_ShouldReturn201() throws Exception {
        UserRegisterDTO registerDTO = UserRegisterDTO.builder()
                .username("john")
                .email("john@example.com")
                .password("secret123")
                .build();

        when(userService.registerUser(any())).thenReturn(userResponse);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john"));
    }

    // --- Login ---
    @Test
    void loginUser_ShouldReturn200() throws Exception {
        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email("john@example.com")
                .password("secret123")
                .build();

        when(userService.loginUser(any())).thenReturn(loginResponse);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt"));
    }

    // --- Get All Users (ADMIN only) ---
    @Test
    void getAllUsers_ShouldReturn200_WhenAdmin() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/users")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"));
    }

    @Test
    void getAllUsers_ShouldReturn403_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("X-User-Role", "USER"))
                .andExpect(status().isForbidden());
    }

    // --- Get User By ID (Admin or same user) ---
    @Test
    void getUserById_ShouldReturn200_WhenAdmin() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/1")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }

    @Test
    void getUserById_ShouldReturn200_WhenSameUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/1")
                        .header("X-User-Role", "USER")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_ShouldReturn403_WhenDifferentUserAndNotAdmin() throws Exception {
        mockMvc.perform(get("/api/users/2")
                        .header("X-User-Role", "USER")
                        .header("X-User-Id", "1"))
                .andExpect(status().isForbidden());
    }

    // --- Update User (Admin or same user) ---
    @Test
    void updateUser_ShouldReturn200_WhenAdmin() throws Exception {
        UserUpdateDTO updateDTO = UserUpdateDTO.builder().username("newJohn").build();
        when(userService.updateUser(eq(1L), any())).thenReturn(userResponse);

        mockMvc.perform(put("/api/users/1")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_ShouldReturn403_WhenDifferentUserAndNotAdmin() throws Exception {
        UserUpdateDTO updateDTO = UserUpdateDTO.builder().username("newJohn").build();

        mockMvc.perform(put("/api/users/2")
                        .header("X-User-Role", "USER")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    // --- Delete User (Admin or same user) ---
    @Test
    void deleteUser_ShouldReturn204_WhenAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_ShouldReturn403_WhenDifferentUserAndNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/2")
                        .header("X-User-Role", "USER")
                        .header("X-User-Id", "1"))
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteUser(anyLong());
    }
}


 */