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

import CinemaBooking.Group2.dtos.cinema.CinemaRequestDTO;
import CinemaBooking.Group2.dtos.cinema.CinemaResponseDTO;
import CinemaBooking.Group2.service.CinemaService;

@RestController
@RequestMapping("/api/cinemas")
public class CinemaController {

    @Autowired
    private CinemaService service;

    @GetMapping
    public List<CinemaResponseDTO> getAll() {
        return service.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")

    public void create(@RequestBody CinemaRequestDTO dto) {
        service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")

    public void update(@PathVariable int id,
                       @RequestBody CinemaRequestDTO dto) {
        service.update(id, dto);
    }
}

