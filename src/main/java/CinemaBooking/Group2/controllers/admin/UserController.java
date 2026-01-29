package CinemaBooking.Group2.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.admin.CreateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UpdateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UserResponseDTO;
import CinemaBooking.Group2.dtos.auth.UserDTO;
import CinemaBooking.Group2.dtos.booking.BookingDetailResponse;
import CinemaBooking.Group2.dtos.booking.BookingHistoryResponse;
import CinemaBooking.Group2.dtos.client.UpdateAvatarRequestDTO;
import CinemaBooking.Group2.dtos.client.UpdateProfileRequestDTO;
import CinemaBooking.Group2.models.Booking;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.security.AuthUserPrincipal;
import CinemaBooking.Group2.service.BookingService;
import CinemaBooking.Group2.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;

    // ================= CREATE USER =================
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createUser(@RequestBody CreateUserRequestDTO req) {
        userService.createUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Tạo user thành công", null));
    }

    // ================= GET USERS =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsers(@RequestParam(required = false) Integer roleId,
            @RequestParam(required = false) Integer cinemaId) {

        List<UserResponseDTO> data = userService.getUsers(roleId, cinemaId);
        return ResponseEntity.ok(new ApiResponse<>("Success", data));
    }

    // ================= UPDATE USER =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUser(@PathVariable int id, @RequestBody UpdateUserRequestDTO req) {

        userService.updateUser(id, req);
        return ResponseEntity.ok(new ApiResponse<>("Cập nhật user thành công", null));
    }

    // ================= LOCK USER =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> lockUser(@PathVariable int id) {
        userService.lockUser(id);
        return ResponseEntity.ok(new ApiResponse<>("Đã khóa tài khoản", null));
    }

    // ================= GET USER DETAIL =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserDetail(@PathVariable int id) {
        UserResponseDTO data = userService.getUserDetail(id);
        return ResponseEntity.ok(new ApiResponse<>("Success", data));
    }

    // ================= GET CURRENT USER =================
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal))
            throw new RuntimeException("Bạn chưa đăng nhập!");

        User user = userService.findByEmail(principal.email());
        System.out.println(user.getAvatarUrl());
        if (user == null)
            throw new RuntimeException("Không tìm thấy người dùng!");

        UserDTO dto = new UserDTO(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(),
                user.getAvatarUrl(), user.getRoleName(), user.getCreatedAt(), user.getCinemaId());
        return ResponseEntity.ok(new ApiResponse<>("Success", dto));
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMyProfile(@RequestBody UpdateProfileRequestDTO req) {
        userService.updateMyProfile(req);
        return ResponseEntity.ok(new ApiResponse<>("Cập nhật thông tin thành công", null));
    }

    // ================= UPDATE AVATAR =================
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> updateMyAvatar(
            @ModelAttribute UpdateAvatarRequestDTO dto) {
        if (dto.getAvatar() == null || dto.getAvatar().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Avatar không được để trống", null));
        }

        String avatarUrl = userService.uploadAvatar(dto.getAvatar());
        return ResponseEntity.ok(new ApiResponse<>("Cập nhật avatar thành công", avatarUrl));
    }

    @GetMapping("/me/bookings")
    public ResponseEntity<PageResponse<BookingHistoryResponse>> getMyBookings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(required = false) String movieTitle,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Booking.PaymentStatus status

    ) {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new RuntimeException("Bạn chưa đăng nhập!");
        }

        User user = userService.findByEmail(principal.email());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        PageResponse<BookingHistoryResponse> response = bookingService.getMyBookingHistory(
                user.getId(), startDate, endDate, status, movieTitle, page, perPage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/bookings/{code}")
    public ResponseEntity<ApiResponse<BookingDetailResponse>> getMyBookingByCode(
            @PathVariable String code
    ) {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new RuntimeException("Bạn chưa đăng nhập!");
        }

        User user = userService.findByEmail(principal.email());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        BookingDetailResponse booking = bookingService.getMyBookingByCode(user.getId(), code);

        return ResponseEntity.ok(new ApiResponse<>("Success", booking));
    }
}