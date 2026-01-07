package CinemaBooking.Group2.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.room.RoomRequestDTO;
import CinemaBooking.Group2.dtos.room.RoomResponseDTO;
import CinemaBooking.Group2.service.RoomService;

@RestController
@RequestMapping("/api")
public class RoomController {

    @Autowired
    private RoomService service;

    @GetMapping("/cinemas/{id}/rooms")
    public List<RoomResponseDTO> getRooms(@PathVariable int id) {
        return service.getByCinema(id);
    }

    @PostMapping("/rooms")
    @PreAuthorize("hasAuthority('ADMIN')")

    public void create(@RequestBody RoomRequestDTO dto) {
        service.create(dto);
    }

    @PutMapping("/rooms/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")

    public void update(@PathVariable int id,
                       @RequestBody RoomRequestDTO dto) {
        service.update(id, dto);
    }
}

