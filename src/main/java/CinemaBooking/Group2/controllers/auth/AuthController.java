package CinemaBooking.Group2.controllers.auth;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.auth.AuthRequestDTO;
import CinemaBooking.Group2.dtos.auth.RegisterRequestDTO;
import CinemaBooking.Group2.dtos.auth.SendOtpRequestDTO;
import CinemaBooking.Group2.service.AuthService;
import CinemaBooking.Group2.service.ChangePasswordService;
import CinemaBooking.Group2.service.ForgotPasswordService;
import CinemaBooking.Group2.service.RegisterService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private RegisterService registerService;
    @Autowired private AuthService authService;
    @Autowired private ForgotPasswordService forgotService;
    @Autowired private ChangePasswordService changeService;

    // ================= REGISTER =================

    @PostMapping("/register/send-otp")
    public ResponseEntity<PageResponse<Void>> sendRegisterOtp(
            @RequestBody SendOtpRequestDTO req) {

        registerService.sendOtp(req.getEmail());

        PageResponse<Void> res = new PageResponse<>();
        res.setMessage("Đã gửi OTP đăng ký");
        res.setData(null);

        return ResponseEntity.ok(res);
    }


    @PostMapping("/register/verify")
    public ResponseEntity<PageResponse<Void>> verifyRegister(
            @RequestBody Map<String, String> body) {

        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail(body.get("email"));
        req.setFullName(body.get("fullName"));
        req.setPassword(body.get("password"));
        req.setPhone(body.get("phone"));

        registerService.verify(req, body.get("otp"));

        PageResponse<Void> res = new PageResponse<>();
        res.setMessage("Đăng ký thành công");
        res.setData(null);

        return ResponseEntity.ok(res);
    }


    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @RequestBody AuthRequestDTO req) {

        Map<String, String> tokens =
                authService.login(req.getEmail(), req.getPassword());

        ApiResponse<Map<String, String>> res = new ApiResponse<>("Đăng nhập thành công", tokens);
        res.setMessage("Đăng nhập thành công");
        res.setData(tokens);
        return ResponseEntity.ok(res);
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(
            @RequestBody Map<String, String> req) {

        Map<String, String> tokens =
                authService.refresh(req.get("refreshToken"));

//        ApiResponse<Map<String, String>> res = new ApiResponse<Map<String, String>>();
//        res.setMessage("Refresh token thành công");
//        res.setData(tokens);

        return ResponseEntity.ok(new ApiResponse<>("Refresh token thành công", tokens));
    }


    @PostMapping("/logout")
    public ResponseEntity<PageResponse<Void>> logout(
            @RequestBody Map<String, String> req) {

        authService.logout(req.get("refreshToken"));

        PageResponse<Void> res = new PageResponse<>();
        res.setMessage("Đăng xuất thành công");
        res.setData(null);

        return ResponseEntity.ok(res);
    }


    // ================= FORGOT PASSWORD =================

    @PostMapping("/forgot/send-otp")
    public ResponseEntity<PageResponse<Void>> sendForgotOtp(
            @RequestBody Map<String, String> body) {

        forgotService.sendOtp(body.get("email"));

        PageResponse<Void> res = new PageResponse<>();
        res.setMessage("OTP đã được gửi tới email");
        res.setData(null);

        return ResponseEntity.ok(res);
    }


    @PostMapping("/forgot/verify")
    public ResponseEntity<PageResponse<Void>> resetPassword(
            @RequestBody Map<String, String> body) {

        forgotService.verifyAndChangePassword(
                body.get("email"),
                body.get("otp"),
                body.get("newPassword")
        );

        PageResponse<Void> res = new PageResponse<>();
        res.setMessage("Đổi mật khẩu thành công");
        res.setData(null);

        return ResponseEntity.ok(res);
    }


    // ================= CHANGE PASSWORD (PROTECTED) =================

    @PostMapping("/password/send-otp")
    public ResponseEntity<PageResponse<Void>> sendChangePasswordOtp() {

        changeService.sendOtp();

        PageResponse<Void> res = new PageResponse<>();
        res.setMessage("OTP đổi mật khẩu đã được gửi");
        res.setData(null);

        return ResponseEntity.ok(res);
    }


    @PostMapping("/password/verify")
    public ResponseEntity<PageResponse<Void>> changePassword(
            @RequestBody Map<String, String> body) {

        changeService.changePassword(
                body.get("otp"),
                body.get("newPassword")
        );

        PageResponse<Void> res = new PageResponse<>();
        res.setMessage("Đổi mật khẩu thành công");
        res.setData(null);

        return ResponseEntity.ok(res);
    }

}

