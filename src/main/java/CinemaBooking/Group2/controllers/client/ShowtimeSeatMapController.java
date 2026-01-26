package CinemaBooking.Group2.controllers.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.showtime.ShowtimeDetailDtos;
import CinemaBooking.Group2.service.ShowtimeSeatMapService;

@RestController
@RequestMapping("/api/showtimes-seat/")
public class ShowtimeSeatMapController {

    private final ShowtimeSeatMapService service;

    public ShowtimeSeatMapController(ShowtimeSeatMapService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeDetailDtos>> getShowtimeDetail(@PathVariable("id") int id) {
        try {
            if (id <= 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("showtimeId invalid.", null));
            }

            ShowtimeDetailDtos data = service.getShowtimeDetailWithSeatMap(id);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse<>("GET SHOWTIME DETAIL SUCCESS.", data));

        } catch (IllegalArgumentException e) {
            // phân loại 404 vs 400 theo message
            String msg = (e.getMessage() == null) ? "BAD_REQUEST." : e.getMessage();

            if ("showtime not found.".equalsIgnoreCase(msg.trim())) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(msg, null));
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(msg, null));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("INTERNAL_SERVER_ERROR.", null));
        }
    }
}
