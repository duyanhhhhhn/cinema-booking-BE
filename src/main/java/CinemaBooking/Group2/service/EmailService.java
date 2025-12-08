package CinemaBooking.Group2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
	public class EmailService {

	    @Autowired
	    private ResendClient resendClient;

	    public void sendOtp(String to, String otp) {
	        String html = "<h3>Mã OTP của bạn là:</h3>" +
	                      "<h1 style='color:red;'>" + otp + "</h1>" +
	                      "<p>Có hiệu lực trong 5 phút.</p>";

	        resendClient.sendEmail(to, "Xác thực đăng ký tài khoản", html);
	    }
	}




