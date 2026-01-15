//package CinemaBooking.Group2.controllers.auth;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import CinemaBooking.Group2.dtos.auth.RegisterRequestDTO;
//import CinemaBooking.Group2.dtos.auth.SendOtpRequestDTO;
//import CinemaBooking.Group2.service.RegisterService;
//import CinemaBooking.Group2.service.AuthService;
//import CinemaBooking.Group2.service.ChangePasswordService;
//import CinemaBooking.Group2.service.ForgotPasswordService;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    @Autowired private RegisterService registerService;
//    @Autowired private AuthService authService;
//    @Autowired private ForgotPasswordService forgotService;
//    @Autowired private ChangePasswordService changeService;
//
//
//    @PostMapping("/register/send-otp")
//    public ResponseEntity<?> sendOtp(@RequestBody SendOtpRequestDTO req) {
//        registerService.sendOtp(req.getEmail());
//        return ResponseEntity.ok(Map.of("message", "Đã gửi OTP"));
//    }
//
//    @PostMapping("/register/verify")
//    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
//        RegisterRequestDTO req = new RegisterRequestDTO();
//        req.setEmail(body.get("email"));
//        req.setFullName(body.get("fullName"));
//        req.setPassword(body.get("password"));
//        req.setPhone(body.get("phone"));
//
//        registerService.verify(req, body.get("otp"));
//        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công"));
//    }
//
//    // ========== LOGIN ===========
//    @PostMapping("/login")
//    public Map<String, String> login(@RequestBody Map<String,String> req){
//        String[] tokens = authService.login(req.get("email"), req.get("password")).split("\\|");
//        return Map.of(
//                "accessToken", tokens[0],
//                "refreshToken", tokens[1]
//        );
//    }
//
//    @PostMapping("/refresh")
//    public Map<String,String> refresh(@RequestBody Map<String,String> req){
//        String refresh = authService.refresh(req.get("refreshToken"));
//        return Map.of("refreshToken", refresh);
//    }
//
//    @PostMapping("/logout")
//    public Map<String,String> logout(@RequestBody Map<String,String> req){
//        authService.logout(req.get("refreshToken"));
//        return Map.of("message", "Đăng xuất thành công");
//    }
//
//    // ========== FORGOT PASSWORD (Public) ===========
//    @PostMapping("/forgot/send-otp")
//    public ResponseEntity<?> sendForgot(@RequestBody Map<String, String> body) {
//        forgotService.sendOtp(body.get("email"));
//        return ResponseEntity.ok(Map.of("message", "OTP đã được gửi tới email"));
//    }
//
//    @PostMapping("/forgot/verify")
//    public ResponseEntity<?> reset(@RequestBody Map<String, String> body) {
//        forgotService.verifyAndChangePassword(
//                body.get("email"),
//                body.get("otp"),
//                body.get("newPassword")
//        );
//        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
//    }
//
//    // ========== CHANGE PASSWORD (Protected) ===========
//    @PostMapping("/password/send-otp")
//    public ResponseEntity<?> sendChange() {
//        changeService.sendOtp();
//        return ResponseEntity.ok(Map.of("message", "OTP đổi mật khẩu đã được gửi!"));
//    }
//
//    @PostMapping("/password/verify")
//    public ResponseEntity<?> change(@RequestBody Map<String, String> body) {
//        changeService.changePassword(body.get("otp"), body.get("newPassword"));
//        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
//    }
//}
package CinemaBooking.Group2.controllers.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import CinemaBooking.Group2.dtos.ApiResponse;
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
    public ResponseEntity<ApiResponse<Void>> sendRegisterOtp(
            @RequestBody SendOtpRequestDTO req) {

        registerService.sendOtp(req.getEmail());
        return ResponseEntity.ok(
                new ApiResponse<>("Đã gửi OTP đăng ký", null)
        );
    }

    @PostMapping("/register/verify")
    public ResponseEntity<ApiResponse<Void>> verifyRegister(
            @RequestBody Map<String, String> body) {

        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail(body.get("email"));
        req.setFullName(body.get("fullName"));
        req.setPassword(body.get("password"));
        req.setPhone(body.get("phone"));

        registerService.verify(req, body.get("otp"));

        return ResponseEntity.ok(
                new ApiResponse<>("Đăng ký thành công", null)
        );
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @RequestBody AuthRequestDTO req) {

        return ResponseEntity.ok(
                new ApiResponse<>("Đăng nhập thành công",
                        authService.login(req.getEmail(), req.getPassword()))
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(
            @RequestBody Map<String, String> req) {

        return ResponseEntity.ok(
                new ApiResponse<>("Refresh token thành công",
                        authService.refresh(req.get("refreshToken")))
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody Map<String, String> req) {

        authService.logout(req.get("refreshToken"));

        return ResponseEntity.ok(
                new ApiResponse<>("Đăng xuất thành công", null)
        );
    }

    // ================= FORGOT PASSWORD =================

    @PostMapping("/forgot/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendForgotOtp(
            @RequestBody Map<String, String> body) {

        forgotService.sendOtp(body.get("email"));

        return ResponseEntity.ok(
                new ApiResponse<>("OTP đã được gửi tới email", null)
        );
    }

    @PostMapping("/forgot/verify")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody Map<String, String> body) {

        forgotService.verifyAndChangePassword(
                body.get("email"),
                body.get("otp"),
                body.get("newPassword")
        );

        return ResponseEntity.ok(
                new ApiResponse<>("Đổi mật khẩu thành công", null)
        );
    }

    // ================= CHANGE PASSWORD (PROTECTED) =================

    @PostMapping("/password/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendChangePasswordOtp() {

        changeService.sendOtp();

        return ResponseEntity.ok(
                new ApiResponse<>("OTP đổi mật khẩu đã được gửi", null)
        );
    }

    @PostMapping("/password/verify")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody Map<String, String> body) {

        changeService.changePassword(
                body.get("otp"),
                body.get("newPassword")
        );

        return ResponseEntity.ok(
                new ApiResponse<>("Đổi mật khẩu thành công", null)
        );
    }
}

