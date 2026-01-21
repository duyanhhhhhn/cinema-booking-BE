package CinemaBooking.Group2.ultis;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
