package CinemaBooking.Group2.controllers.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.auth.RegisterRequestDTO;
import CinemaBooking.Group2.service.RegisterService;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired RegisterService registerService;

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody RegisterRequestDTO req) {
        registerService.sendOtp(req);
        return ResponseEntity.ok("Đã gửi OTP");
    }

    @PostMapping("/register/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail(body.get("email"));
        req.setFullName(body.get("fullName"));
        req.setPassword(body.get("password"));
        req.setPhone(body.get("phone"));

        registerService.verify(req, body.get("otp"));
        return ResponseEntity.ok("Đăng ký thành công");
    }
}


