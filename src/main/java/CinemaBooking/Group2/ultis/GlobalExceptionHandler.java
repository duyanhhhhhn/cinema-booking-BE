//package CinemaBooking.Group2.ultis;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import io.swagger.v3.oas.annotations.Hidden;
//
//import java.util.Map;
//
//@Hidden
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(AuthException.class)
//    public ResponseEntity<Map<String, String>> handleAuthException(AuthException ex) {
//        // Trả 401 Unauthorized + message từ exception
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                .body(Map.of("error", ex.getMessage()));
//    }
//
//    public ResponseEntity<Map<String, String>> handleOtherExceptions(Exception ex) {
//        // Trả 500 Internal Server Error cho các lỗi khác
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("error", ex.getMessage()));
//    }
//}

package CinemaBooking.Group2.ultis;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.Map;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== AUTH ERROR (LOGIN / REFRESH / LOGOUT) =====
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuthException(AuthException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "message", ex.getMessage()
                ));
    }

    // ===== OTHER ERROR =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtherExceptions(Exception ex) {

        ex.printStackTrace(); // LOG để debug

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message", "Lỗi hệ thống"
                ));
    }
}

