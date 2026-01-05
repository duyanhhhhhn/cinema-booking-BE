package CinemaBooking.Group2.controllers.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.booking.BookingCalculateRequest;
import CinemaBooking.Group2.dtos.booking.BookingCalculateResponse;
import CinemaBooking.Group2.dtos.booking.BookingCreateRequest;
import CinemaBooking.Group2.dtos.booking.BookingCreateResponse;
import CinemaBooking.Group2.service.BookingService;
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
    @Operation(
        summary = "Calculate booking price",
        description = "Tính toán tổng tiền tạm tính cho booking. Logic: Giá vé + Ghế VIP + Combo - Voucher + Phụ thu Lễ"
    )
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
    @Operation(
        summary = "Create new booking",
        description = "Tạo đơn hàng mới với trạng thái PENDING. Booking sẽ giữ ghế cho khách hàng trong thời gian thanh toán."
    )
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
