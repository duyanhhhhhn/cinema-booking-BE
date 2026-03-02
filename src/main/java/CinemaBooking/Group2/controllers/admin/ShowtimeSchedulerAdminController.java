package CinemaBooking.Group2.controllers.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.scheduler.AdminCreateShowtimeReqDto;
import CinemaBooking.Group2.dtos.scheduler.AdminMoveShowtimeReqDto;
import CinemaBooking.Group2.service.ShowtimeSchedulerAdminService;

@RestController
@RequestMapping("/api/admin/showtime-scheduler")
public class ShowtimeSchedulerAdminController {

    private final ShowtimeSchedulerAdminService service;

    public ShowtimeSchedulerAdminController(ShowtimeSchedulerAdminService service) {
        this.service = service;
    }

    @GetMapping("/scheduler")
    public ResponseEntity<ApiResponse<Object>> scheduler(
            @RequestParam int cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        try {
            var result = service.getScheduler(cinemaId, date);

            var meta = new java.util.LinkedHashMap<String, Object>();
            meta.put("timelineStart", result.timelineStart.toString());
            meta.put("timelineEnd", result.timelineEnd.toString());
            meta.put("totalConflicts", result.totalConflicts);

            return ResponseEntity.ok(new ApiResponse<>("OK", result.data, meta));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));

        } catch (IllegalStateException e) {
            String msg = e.getMessage();
            if ("SHOWTIME_NOT_FOUND".equals(msg)) {
                return ResponseEntity.status(404).body(new ApiResponse<>(msg, null));
            }
            if ("CONFLICT_SHOWTIME_OVERLAP".equals(msg)) {
                return ResponseEntity.status(409).body(new ApiResponse<>(msg, null));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>(msg, null));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("INTERNAL_SERVER_ERROR", null));
        }
    }

    // ✅ POST dạng param để Swagger hiện ô nhập (không JSON body)
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestParam int cinemaId,
            @RequestParam int roomId,
            @RequestParam int movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam BigDecimal basePrice
    ) {
        try {
            AdminCreateShowtimeReqDto req = new AdminCreateShowtimeReqDto();
            req.setCinemaId(cinemaId);
            req.setRoomId(roomId);
            req.setMovieId(movieId);
            req.setStartAt(startAt);
            req.setBasePrice(basePrice);

            int id = service.createShowtime(req);
            return ResponseEntity.status(201).body(new ApiResponse<>("CREATED", java.util.Map.of("id", id)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));

        } catch (IllegalStateException e) {
            String msg = e.getMessage();
            if ("CONFLICT_SHOWTIME_OVERLAP".equals(msg)) {
                return ResponseEntity.status(409).body(new ApiResponse<>(msg, null));
            }
            if ("SHOWTIME_NOT_FOUND".equals(msg)) {
                return ResponseEntity.status(404).body(new ApiResponse<>(msg, null));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>(msg, null));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("INTERNAL_SERVER_ERROR", null));
        }
    }

    // ✅ PATCH move dạng param để Swagger hiện ô nhập
    @PatchMapping("/{id}/move")
    public ResponseEntity<ApiResponse<Object>> move(
            @PathVariable int id,
            @RequestParam int cinemaId,
            @RequestParam int roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt
    ) {
        try {
            AdminMoveShowtimeReqDto req = new AdminMoveShowtimeReqDto();
            req.setCinemaId(cinemaId);
            req.setRoomId(roomId);
            req.setStartAt(startAt);

            service.moveShowtime(id, req);
            return ResponseEntity.ok(new ApiResponse<>("MOVED", java.util.Map.of("id", id)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));

        } catch (IllegalStateException e) {
            String msg = e.getMessage();
            if ("SHOWTIME_NOT_FOUND".equals(msg)) {
                return ResponseEntity.status(404).body(new ApiResponse<>(msg, null));
            }
            if ("CONFLICT_SHOWTIME_OVERLAP".equals(msg)) {
                return ResponseEntity.status(409).body(new ApiResponse<>(msg, null));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>(msg, null));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("INTERNAL_SERVER_ERROR", null));
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Object>> cancel(@PathVariable int id) {
        try {
            service.cancelShowtime(id);
            return ResponseEntity.ok(new ApiResponse<>("CANCELLED", java.util.Map.of("id", id)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));

        } catch (IllegalStateException e) {
            String msg = e.getMessage();
            if ("SHOWTIME_NOT_FOUND".equals(msg)) {
                return ResponseEntity.status(404).body(new ApiResponse<>(msg, null));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>(msg, null));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("INTERNAL_SERVER_ERROR", null));
        }
    }
    
    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<Object>> movies(@RequestParam(required = false) String keyword) {
        try {
            var data = service.getMovieOptions(keyword);
            return ResponseEntity.ok(new ApiResponse<>("OK", data));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("INTERNAL_SERVER_ERROR", null));
        }
    }
}