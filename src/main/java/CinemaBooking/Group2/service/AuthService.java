package CinemaBooking.Group2.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    // ================= LOGIN =================
    public Map<String, String> login(String email, String password) {

        User user = userRepo.findByEmail(email);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Sai tài khoản hoặc mật khẩu");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(email);

        refreshRepo.saveToken(user.getId(), refreshToken);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

    // ================= REFRESH =================
    public Map<String, String> refresh(String refreshToken) {

        // 1. Validate JWT
        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token không hợp lệ");
        }

        // 2. Check DB
        if (!refreshRepo.isValid(refreshToken)) {
            throw new RuntimeException("Refresh token đã bị thu hồi");
        }

        // 3. Get user
        String email = jwtService.getEmail(refreshToken);
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User không tồn tại");
        }

        // 4. Rotate refresh token
        refreshRepo.revoke(refreshToken);

        String newRefreshToken = jwtService.generateRefreshToken(email);
        refreshRepo.saveToken(user.getId(), newRefreshToken);

        // 5. Generate new access token
        String newAccessToken = jwtService.generateAccessToken(user);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }

    // ================= LOGOUT =================
    public void logout(String refreshToken) {
        refreshRepo.revoke(refreshToken);
    }
}

