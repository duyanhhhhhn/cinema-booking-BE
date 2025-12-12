package CinemaBooking.Group2.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.models.Otp;
import CinemaBooking.Group2.repositories.OtpRepository;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.security.AuthUserPrincipal;

@Service
public class ChangePasswordService {

    @Autowired private OtpRepository otpRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private EmailService emailService;
    @Autowired private PasswordEncoder encoder;

    // Gửi OTP đổi mật khẩu (đã login)
    public void sendOtp() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new RuntimeException("Bạn chưa đăng nhập!");
        }

        String email = principal.email(); // Lấy email

        String otp = String.format("%06d", new Random().nextInt(999999));

        otpRepo.save(new Otp(
            email,
            otp,
            Otp.OtpPurpose.TWO_FACTOR,
            LocalDateTime.now().plusMinutes(5)
        ));

        emailService.sendOtp(email, otp);
    }


    // Xác thực OTP và đổi mật khẩu
    public void changePassword(String otpCode, String newPassword) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new RuntimeException("Bạn chưa đăng nhập!");
        }

        String email = principal.email();

        var otpOptional = otpRepo.findLatestValid(email, Otp.OtpPurpose.TWO_FACTOR);
        Otp otp = otpOptional.orElseThrow(() -> 
            new RuntimeException("OTP không hợp lệ hoặc đã hết hạn")
        );

        var user = userRepo.findByEmail(email);
        userRepo.updatePassword(user.getId(), encoder.encode(newPassword));

        otpRepo.attachUserAndMarkUsed(otp.getId(), user.getId());
    }

}
