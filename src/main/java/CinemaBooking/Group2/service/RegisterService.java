package CinemaBooking.Group2.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.auth.RegisterRequestDTO;
import CinemaBooking.Group2.models.Otp;
import CinemaBooking.Group2.repositories.OtpRepository;
import CinemaBooking.Group2.repositories.UserRepository;

@Service
public class RegisterService {

    @Autowired 
    private UserRepository userRepo;
    
    @Autowired 
    private OtpRepository otpRepo;
    
    @Autowired 
    private EmailService emailService;
    
    @Autowired 
    private PasswordEncoder encoder;

    /**
     * Gửi OTP đăng ký (không tạo user trước)
     */
    public void sendOtp(String email) {
        if (userRepo.existsByEmail(email)) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Rate Limit: 3 OTP / 10 phút
        int count = otpRepo.countRecentRequests(email, 10, Otp.OtpPurpose.REGISTER);
        if (count >= 3) {
            throw new RuntimeException("Bạn đã gửi quá nhiều OTP, vui lòng thử lại sau ít phút.");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        otpRepo.save(new Otp(
            email,
            otp,
            Otp.OtpPurpose.REGISTER,
            LocalDateTime.now().plusMinutes(5)
        ));

        emailService.sendOtp(email, otp);
    }



    /**
     * Xác thực OTP & tạo user
     */
    public void verify(RegisterRequestDTO req, String otpCode) {

        // 1) Lấy OTP
        var otpOptional = otpRepo.findLatestValid(req.getEmail(), Otp.OtpPurpose.REGISTER);

        Otp otp = otpOptional.orElseThrow(() -> 
            new RuntimeException("OTP không hợp lệ hoặc đã hết hạn")
        );

        // 2) Tạo user sau khi OTP hợp lệ
        int userId = userRepo.createUser(req, encoder.encode(req.getPassword()));

        if (userId <= 0) {
            throw new RuntimeException("Không thể tạo tài khoản. Vui lòng thử lại.");
        }
        // 3) Gắn user vào OTP + đánh dấu đã sử dụng
        otpRepo.attachUserAndMarkUsed(otp.getId(), userId);
    }

}
