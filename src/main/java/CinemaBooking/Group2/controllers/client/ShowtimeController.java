package CinemaBooking.Group2.controllers.client;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import CinemaBooking.Group2.dtos.showtime.MovieShowtimeGroupDtos;
import CinemaBooking.Group2.service.ShowtimeService;

@RestController
@RequestMapping("/api/showtimes/public")
public class ShowtimeController {

    private final ShowtimeService stServices;

    public ShowtimeController(ShowtimeService stServices) {
        this.stServices = stServices;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<?> getAllShowtime(
            @RequestParam int cinemaId,
            @RequestParam(required = false) Integer movieId,
            @RequestParam String date
    ) {
        try {
            LocalDate d;
            try {
                d = LocalDate.parse(date);
            } catch (DateTimeParseException ex) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("date invalid. Expected format: yyyy-MM-dd.");
            }

            List<MovieShowtimeGroupDtos> data = stServices.getShowtimesGroupedByMovie(cinemaId, movieId, d);
            return ResponseEntity.status(HttpStatus.OK).body(data);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("INTERNAL_SERVER_ERROR.");
        }
    }
}
