package CinemaBooking.Group2.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.repositories.RefreshTokenRepository;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.security.JwtService;
import CinemaBooking.Group2.ultis.AuthException;

@Service
public class AuthService {

    @Autowired private UserRepository userRepo;
    @Autowired private JwtService jwtService;
    @Autowired private RefreshTokenRepository refreshRepo;
    @Autowired private PasswordEncoder encoder;

    // ================= LOGIN =================
    public Map<String, String> login(String email, String password) {

        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new AuthException("Email không tồn tại");
        }

        if (!encoder.matches(password, user.getPassword())) {
            throw new AuthException("Mật khẩu không đúng");
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
        if (!jwtService.validateToken(refreshToken)) {
            throw new AuthException("Refresh token không hợp lệ");
        }

        if (!refreshRepo.isValid(refreshToken)) {
            throw new AuthException("Refresh token đã bị thu hồi");
        }

        String email = jwtService.getEmail(refreshToken);
        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new AuthException("User không tồn tại");
        }

        // Xoay refresh token
        refreshRepo.revoke(refreshToken);
        String newRefreshToken = jwtService.generateRefreshToken(email);
        refreshRepo.saveToken(user.getId(), newRefreshToken);

        String newAccessToken = jwtService.generateAccessToken(user);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }

    // ================= LOGOUT =================
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AuthException("Refresh token không được để trống");
        }
        refreshRepo.revoke(refreshToken);
    }
}
