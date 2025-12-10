package CinemaBooking.Group2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.models.RefreshToken;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.repositories.RefreshTokenRepository;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.security.JwtService;

@Service
public class AuthService {

    @Autowired private UserRepository userRepo;
    @Autowired private JwtService jwtService;
    @Autowired private RefreshTokenRepository refreshRepo;
    @Autowired private PasswordEncoder encoder;

    public String login(String email, String password) {
        User u = userRepo.findByEmail(email);
        if (u == null || !encoder.matches(password, u.getPassword())) {
            throw new RuntimeException("Sai tài khoản hoặc mật khẩu");
        }

        String access = jwtService.generateAccessToken(u);
        String refresh = jwtService.generateRefreshToken(email);

        refreshRepo.saveToken(u.getId(), refresh);

        return access + "|" + refresh; // Controller sẽ format lại JSON
    }

    public String refresh(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token không hợp lệ");
        }

        if (!refreshRepo.isValid(refreshToken)) {
            throw new RuntimeException("Refresh token đã bị thu hồi");
        }

        String email = jwtService.getEmail(refreshToken);
        refreshRepo.revoke(refreshToken);

        String newToken = jwtService.generateRefreshToken(email);
        refreshRepo.saveToken(userRepo.findByEmail(email).getId(), newToken);

        return newToken;
    }

    public void logout(String refreshToken) {
        refreshRepo.revoke(refreshToken);
    }
}
