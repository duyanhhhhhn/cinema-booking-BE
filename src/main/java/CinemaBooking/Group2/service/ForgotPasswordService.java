package CinemaBooking.Group2.service;


import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.models.Otp;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.repositories.OtpRepository;
import CinemaBooking.Group2.repositories.UserRepository;

@Service
public class ForgotPasswordService {

    @Autowired private UserRepository userRepo;
    @Autowired private OtpRepository otpRepo;
    @Autowired private EmailService emailService;    
    @Autowired private PasswordEncoder encoder;

    /** B1: Gửi OTP reset password */
    public void sendOtp(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) throw new RuntimeException("Email không tồn tại");

        // Rate Limit: 3 OTP / 10 phút
        int count = otpRepo.countRecentRequests(email, 10, Otp.OtpPurpose.FORGOT_PASSWORD);
        if (count >= 3) {
            throw new RuntimeException("Bạn đã gửi quá nhiều OTP, vui lòng thử lại sau ít phút.");
        }

        String code = String.format("%06d", new Random().nextInt(999999));

        otpRepo.save(new Otp(
                email,
                code,
                Otp.OtpPurpose.FORGOT_PASSWORD,
                LocalDateTime.now().plusMinutes(5)
        ));

        emailService.sendOtp(email, code);
    }


    /** B2: Xác thực OTP + đổi mật khẩu */
    public void verifyAndChangePassword(String email, String otpCode, String newPassword) {
        var otpOptional = otpRepo.findLatestValid(email, Otp.OtpPurpose.FORGOT_PASSWORD);

        Otp otp = otpOptional.orElseThrow(() -> 
            new RuntimeException("OTP không hợp lệ hoặc đã hết hạn")
        );

        // Xác nhận đúng code không
        if (!otp.getCode().equals(otpCode)) {
            throw new RuntimeException("Mã OTP không đúng");
        }

        // Update password
        User user = userRepo.findByEmail(email);
        userRepo.updatePassword(user.getId(), encoder.encode(newPassword));

        // Đánh dấu OTP đã dùng
        otpRepo.attachUserAndMarkUsed(otp.getId(), user.getId());
    }
}

