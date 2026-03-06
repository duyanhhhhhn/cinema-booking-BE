package CinemaBooking.Group2.controllers.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.booking.BookingCalculateRequest;
import CinemaBooking.Group2.dtos.booking.BookingCalculateResponse;
import CinemaBooking.Group2.dtos.booking.BookingCreateRequest;
import CinemaBooking.Group2.dtos.booking.BookingCreateResponse;
import CinemaBooking.Group2.dtos.booking.BookingDetailResponse;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.security.AuthUserPrincipal;
import CinemaBooking.Group2.service.BookingService;
import CinemaBooking.Group2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
@Validated
@Tag(name = "Booking", description = "Booking management APIs")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate booking price", description = "Tính toán tổng tiền tạm tính cho booking. Logic: Giá vé + Ghế VIP + Combo - Voucher + Phụ thu Lễ")
    public ResponseEntity<?> calculateBooking(@Valid @RequestBody BookingCalculateRequest request) {
        try {
            BookingCalculateResponse response = bookingService.calculateBooking(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while calculating booking: " + e.getMessage()));
        }
    }

    @PostMapping("/create")
    @Operation(summary = "Create new booking", description = "Tạo đơn hàng mới với trạng thái PENDING. Booking sẽ giữ ghế cho khách hàng trong thời gian thanh toán.")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingCreateRequest request) {
        try {
            BookingCreateResponse response = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while creating booking: " + e.getMessage()));
        }
    }

    @GetMapping("/detail/{bookingCode}")
    @Operation(summary = "Get booking detail by booking code", description = "Lấy chi tiết vé theo mã booking. Dùng cho trang kết quả thanh toán.")
    public ResponseEntity<?> getBookingByCode(@PathVariable String bookingCode) {
        try {
            BookingDetailResponse response = bookingService.getBookingByCodeAdmin(bookingCode);
            return ResponseEntity.ok(new ApiResponse<>("Success", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while fetching booking: " + e.getMessage()));
        }
    }

    private static class ErrorResponse {
        private String error;
        private long timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}