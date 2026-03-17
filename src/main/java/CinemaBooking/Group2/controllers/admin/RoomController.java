package CinemaBooking.Group2.controllers.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.room.RoomDetailDTO;
import CinemaBooking.Group2.dtos.room.RoomRequestDTO;
import CinemaBooking.Group2.dtos.room.RoomResponseDTO;
import CinemaBooking.Group2.dtos.room.SeatLayoutUpdateDTO;
import CinemaBooking.Group2.service.RoomService;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    // ================= GET ROOMS (OPTIONAL BY CINEMA) =================
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponseDTO>>> getRooms(
            @RequestParam(required = false) Integer cinemaId) {

        List<RoomResponseDTO> data = cinemaId == null
                ? service.getAllRooms()
                : service.getByCinema(cinemaId);

        return ResponseEntity.ok(new ApiResponse<>("Success", data));
    }

    // ================= GET ROOMS BY CINEMA =================
    @GetMapping("/cinemas/{cinemaId}/rooms")
    public ResponseEntity<ApiResponse<List<RoomResponseDTO>>> getRoomsByCinema(
            @PathVariable int cinemaId) {

        List<RoomResponseDTO> data = service.getByCinema(cinemaId);
        return ResponseEntity.ok(new ApiResponse<>("Success", data));
    }

    // ================= GET ROOM DETAIL =================
    @GetMapping("/rooms/{id}")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> getRoomDetail(@PathVariable int id) {
        RoomResponseDTO data = service.getRoomDetail(id);
        return ResponseEntity.ok(new ApiResponse<>("Success", data));
    }
    
    @GetMapping("/rooms/client/{id}")
    public ResponseEntity<ApiResponse<RoomDetailDTO>> getRoomDetailForClient(@PathVariable int id) {
        RoomDetailDTO data = service.getRoomDetailForClient(id);
        return ResponseEntity.ok(new ApiResponse<>("Success", data));
    }

    // Added alias for legacy client path
    @GetMapping("/client/rooms/{id}")
    public ResponseEntity<ApiResponse<RoomDetailDTO>> getRoomDetailForClientAlias(@PathVariable int id) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Invalid room id", null));
        }
        RoomDetailDTO data = service.getRoomDetailForClient(id);
        return ResponseEntity.ok(new ApiResponse<>("Success", data));
    }

    // ================= CREATE ROOM =================
    @PostMapping("/rooms")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> create(@RequestBody RoomRequestDTO dto) {
        RoomResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Room created", created));
    }

    // ================= UPDATE ROOM =================
    @PutMapping("/rooms/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> update(
            @PathVariable int id,
            @RequestBody RoomRequestDTO dto) {

        RoomResponseDTO updated = service.update(id, dto);
        return ResponseEntity.ok(new ApiResponse<>("Room updated", updated));
    }
    
    // ================= UPDATE SEAT LAYOUT =================
    @PutMapping("/rooms/{id}/seat-layout")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> updateSeatMap(
            @PathVariable int id,
            @RequestBody SeatLayoutUpdateDTO dto) {

        RoomResponseDTO updated = service.updateSeatLayout(id, dto);
        return ResponseEntity.ok(new ApiResponse<>("Seat map updated", updated));
    }

    // ================= DELETE ROOM =================
    @DeleteMapping("/rooms/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.ok(new ApiResponse<>("Room deleted", null));
    }
}