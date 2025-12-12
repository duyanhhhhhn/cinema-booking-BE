package CinemaBooking.Group2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.auth.UserDTO;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.security.AuthUserPrincipal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/me")
    public UserDTO getCurrentUser() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new RuntimeException("Bạn chưa đăng nhập!");
        }

        //  Lấy email từ JWT
        String email = principal.email();

        // Lấy user từ DB
        User user = userRepo.findByEmail(email);
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
