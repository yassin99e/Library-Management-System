package ma.ensa.borrower_ms.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.ensa.borrower_ms.dto.*;
import ma.ensa.borrower_ms.entity.Role;
import ma.ensa.borrower_ms.exception.ForbiddenException;
import ma.ensa.borrower_ms.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ Public
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegisterDTO request) {
        return ResponseEntity.status(201).body(userService.registerUser(request));
    }

    // ✅ Public
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> loginUser(@Valid @RequestBody UserLoginDTO request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    // ✅ Admin only
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ForbiddenException("Access denied: Only ADMINs can view all users.");
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ✅ Admin or same user
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id, HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");
        if (!"ADMIN".equalsIgnoreCase(role) && !String.valueOf(id).equals(userId)) {
            throw new ForbiddenException("Access denied: You can only view your own account.");
        }
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ✅ Admin or same user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO requestDto,
            HttpServletRequest request) {

        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");
        if (!"ADMIN".equalsIgnoreCase(role) && !String.valueOf(id).equals(userId)) {
            throw new ForbiddenException("Access denied: You can only update your own account.");
        }

        return ResponseEntity.ok(userService.updateUser(id, requestDto));
    }

    // ✅ Admin or same user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");
        if (!"ADMIN".equalsIgnoreCase(role) && !String.valueOf(id).equals(userId)) {
            throw new ForbiddenException("Access denied: You can only delete your own account.");
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/ids/users")
    public ResponseEntity<List<Long>> GetUsersIds(){
        return ResponseEntity.ok(userService.findIdsByRole(Role.USER));
    }

    @GetMapping("/ids/admins")
    public ResponseEntity<List<Long>> GetAdminIds(){
        return ResponseEntity.ok(userService.findIdsByRole(Role.ADMIN));
    }


}
