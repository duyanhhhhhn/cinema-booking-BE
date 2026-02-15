package CinemaBooking.Group2.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.booking.BookingDetailResponse;
import CinemaBooking.Group2.dtos.booking.WalkInBookingRequest;
import CinemaBooking.Group2.dtos.booking.WalkInBookingResponse;
import CinemaBooking.Group2.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/bookings")
@Tag(name = "Admin - Booking", description = "Admin Booking management APIs")
public class BookingAdminController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/{code}")
    @Operation(summary = "Get booking by code (Admin)", description = "Lấy thông tin chi tiết booking theo mã booking. API dành cho admin để kiểm tra vé của người dùng đã đặt.")
    public ResponseEntity<ApiResponse<BookingDetailResponse>> getBookingByCodeAdmin(
            @PathVariable String code) {
        BookingDetailResponse booking = bookingService.getBookingByCodeAdmin(code);
        return ResponseEntity.ok(new ApiResponse<>("Success", booking));
    }

    @PostMapping("/walk-in")
    public ResponseEntity<ApiResponse<WalkInBookingResponse>> createWalkInBooking(
            @Valid @RequestBody WalkInBookingRequest request) {
        WalkInBookingResponse booking = bookingService.createWalkInBooking(request);
        return ResponseEntity.ok(new ApiResponse<>("Booking created successfully", booking));
    }
}