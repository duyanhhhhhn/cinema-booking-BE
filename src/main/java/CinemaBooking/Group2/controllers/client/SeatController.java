package CinemaBooking.Group2.controllers.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.seat.HoldSeatRequestDTO;
import CinemaBooking.Group2.dtos.seat.HoldSeatResponseDTO;
import CinemaBooking.Group2.dtos.seat.ReleaseSeatRequestDTO;
import CinemaBooking.Group2.dtos.seat.ReleaseSeatResponseDTO;
import CinemaBooking.Group2.dtos.seat.SeatStatusDTO;
import CinemaBooking.Group2.dtos.showtime.ShowtimeSeatResponseDTO;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.security.AuthUserPrincipal;
import CinemaBooking.Group2.service.SeatBookingFacade;
import CinemaBooking.Group2.service.UserService;
import CinemaBooking.Group2.service.ShowtimeService;

@RestController
@RequestMapping("/api")
public class SeatController {

    @Autowired
    private SeatBookingFacade seatBookingFacade;
    @Autowired
    private UserService userService;
    private ShowtimeService service;

    @GetMapping("/showtimes/{id}/seats")
    public ResponseEntity<?> getSeats(
            @PathVariable int id) {

        ShowtimeSeatResponseDTO data = service.getSeatMap(id);

        return ResponseEntity.ok(data);
    }

    @PostMapping("/booking/hold-seat")
    public ResponseEntity<HoldSeatResponseDTO> holdSeats(
            @Validated @RequestBody HoldSeatRequestDTO request) {
        try {
            Integer userId = getCurrentUserId();
            if (userId == null) {
                HoldSeatResponseDTO response = new HoldSeatResponseDTO(false, "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            HoldSeatResponseDTO response = seatBookingFacade.holdSeats(request, userId);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        } catch (Exception e) {
            HoldSeatResponseDTO response = new HoldSeatResponseDTO(false, "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/booking/release-seat")
    public ResponseEntity<ReleaseSeatResponseDTO> releaseSeats(
            @Validated @RequestBody ReleaseSeatRequestDTO request) {
        try {
            // Get current authenticated user
            Integer userId = getCurrentUserId();
            if (userId == null) {
                ReleaseSeatResponseDTO response = new ReleaseSeatResponseDTO(false, "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            ReleaseSeatResponseDTO response = seatBookingFacade.releaseSeats(request, userId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ReleaseSeatResponseDTO response = new ReleaseSeatResponseDTO(false, "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private Integer getCurrentUserId() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !(auth.getPrincipal() instanceof AuthUserPrincipal principal)) {
                return null;
            }

            User user = userService.findByEmail(principal.email());
            return user != null ? user.getId() : null;

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi để debug nếu cần
            return null;
        }
    }
}
