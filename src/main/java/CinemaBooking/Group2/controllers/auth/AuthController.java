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
import CinemaBooking.Group2.dtos.auth.AuthRequestDTO;
import CinemaBooking.Group2.dtos.auth.AuthResponseDTO;
import CinemaBooking.Group2.service.AuthService;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired RegisterService registerService;
    @Autowired private AuthService authService;

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
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String,String> req){
        String[] tokens = authService.login(req.get("email"), req.get("password")).split("\\|");
        return Map.of(
                "accessToken", tokens[0],
                "refreshToken", tokens[1]
        );
    }

    @PostMapping("/refresh")
    public Map<String,String> refresh(@RequestBody Map<String,String> req){
        String refresh = authService.refresh(req.get("refreshToken"));
        return Map.of("refreshToken", refresh);
    }

    @PostMapping("/logout")
    public Map<String,String> logout(@RequestBody Map<String,String> req){
        authService.logout(req.get("refreshToken"));
        return Map.of("message", "Đăng xuất thành công");
    }
    
}


