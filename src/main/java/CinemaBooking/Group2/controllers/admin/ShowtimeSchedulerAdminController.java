package CinemaBooking.Group2.controllers.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.scheduler.AdminCreateShowtimeReqDto;
import CinemaBooking.Group2.dtos.scheduler.AdminMoveShowtimeReqDto;
import CinemaBooking.Group2.service.ShowtimeSchedulerAdminService;
import CinemaBooking.Group2.dtos.scheduler.AdminUpdateShowtimeReqDto;
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
        var result = service.getScheduler(cinemaId, date);

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("timelineStart", result.timelineStart.toString());
        meta.put("timelineEnd", result.timelineEnd.toString());
        meta.put("totalConflicts", result.totalConflicts);

        return ResponseEntity.ok(new ApiResponse<>("OK", result.data, meta));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestParam int cinemaId,
            @RequestParam int roomId,
            @RequestParam int movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam BigDecimal basePrice
    ) {
        AdminCreateShowtimeReqDto req = new AdminCreateShowtimeReqDto();
        req.setCinemaId(cinemaId);
        req.setRoomId(roomId);
        req.setMovieId(movieId);
        req.setStartAt(startAt);
        req.setBasePrice(basePrice);

        int id = service.createShowtime(req);
        return ResponseEntity.status(201).body(new ApiResponse<>("CREATED", Map.of("id", id)));
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<ApiResponse<Object>> move(
            @PathVariable int id,
            @RequestParam int cinemaId,
            @RequestParam int roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt
    ) {
        AdminMoveShowtimeReqDto req = new AdminMoveShowtimeReqDto();
        req.setCinemaId(cinemaId);
        req.setRoomId(roomId);
        req.setStartAt(startAt);

        service.moveShowtime(id, req);
        return ResponseEntity.ok(new ApiResponse<>("MOVED", Map.of("id", id)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Object>> cancel(@PathVariable int id) {
        service.cancelShowtime(id);
        return ResponseEntity.ok(new ApiResponse<>("CANCELLED", Map.of("id", id)));
    }

    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<Object>> movies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roomType
    ) {
        var data = service.getMovieOptions(keyword, roomType);
        return ResponseEntity.ok(new ApiResponse<>("OK", data));
    }
    
    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<Object>> detail(
            @PathVariable int id,
            @RequestParam int cinemaId
    ) {
        var data = service.getShowtimeDetail(id, cinemaId);
        return ResponseEntity.ok(new ApiResponse<>("OK", data));
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<ApiResponse<Object>> edit(
            @PathVariable int id,
            @RequestParam int cinemaId,
            @RequestParam int roomId,
            @RequestParam int movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam BigDecimal basePrice
    ) {
        AdminUpdateShowtimeReqDto req = new AdminUpdateShowtimeReqDto();
        req.setCinemaId(cinemaId);
        req.setRoomId(roomId);
        req.setMovieId(movieId);
        req.setStartAt(startAt);
        req.setBasePrice(basePrice);

        service.editShowtime(id, req);
        return ResponseEntity.ok(new ApiResponse<>("UPDATED", java.util.Map.of("id", id)));
    }
}