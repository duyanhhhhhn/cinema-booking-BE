package CinemaBooking.Group2.controllers.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.admin.CreateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UpdateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UserResponseDTO;
import CinemaBooking.Group2.dtos.auth.UserDTO;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.security.AuthUserPrincipal;
import CinemaBooking.Group2.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping
	public ResponseEntity<?> createUser(@RequestBody CreateUserRequestDTO req) {
		userService.createUser(req);
		return ResponseEntity.ok(Map.of("message", "Tạo user thành công"));
	}
	
	/**
     * Lấy danh sách user
     * - ADMIN: xem tất cả, lọc theo role / cinema tùy ý
     * - MANAGER: chỉ xem user trong rạp của mình
     *            (roleId/cinemaId client gửi sẽ bị service kiểm soát)
     */
    @GetMapping
    public ResponseEntity<?> getUsers(
        @RequestParam(required = false) Integer roleId,
        @RequestParam(required = false) Integer cinemaId
    ) {
        return ResponseEntity.ok(
            userService.getUsers(roleId, cinemaId)
        );
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable int id,
            @RequestBody UpdateUserRequestDTO req
    ) {
        userService.updateUser(id, req);
        return ResponseEntity.ok(Map.of("message", "Cập nhật user thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> lockUser(@PathVariable int id) {
        userService.lockUser(id);
        return ResponseEntity.ok(Map.of("message", "Đã khóa tài khoản"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDetail(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserDetail(id));
    }
    @GetMapping("/me")
    public UserDTO getCurrentUser() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new RuntimeException("Bạn chưa đăng nhập!");
        }

        //  Lấy email từ JWT
        String email = principal.email();

        // Lấy user từ DB
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }
        //  Trả DTO không có password
        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getRoleName(),
                user.getCinemaId()
        );
    }

}
