package CinemaBooking.Group2.controllers.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

}
