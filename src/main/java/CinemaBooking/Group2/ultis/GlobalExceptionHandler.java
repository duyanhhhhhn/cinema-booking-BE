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
import org.springframework.web.server.ResponseStatusException;

import CinemaBooking.Group2.dtos.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== AUTH ERROR (LOGIN / REFRESH / LOGOUT) =====
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {
        ErrorResponse error = ErrorResponse.unauthorized(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ===== RESPONSE STATUS EXCEPTION (BAD_REQUEST, NOT_FOUND, etc.) =====
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : "Đã xảy ra lỗi";

        ErrorResponse error = new ErrorResponse(
                message,
                status.name(),
                status.value()
        );

        return ResponseEntity.status(status).body(error);
    }

    // ===== OTHER ERROR =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
        ex.printStackTrace(); // LOG để debug

        ErrorResponse error = ErrorResponse.internalError("Lỗi hệ thống");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}