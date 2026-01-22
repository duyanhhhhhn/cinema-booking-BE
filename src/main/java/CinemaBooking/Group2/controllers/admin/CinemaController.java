//package CinemaBooking.Group2.controllers.admin;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import CinemaBooking.Group2.dtos.cinema.CinemaRequestDTO;
//import CinemaBooking.Group2.dtos.cinema.CinemaResponseDTO;
//import CinemaBooking.Group2.service.CinemaService;
//
//@RestController
//@RequestMapping("/api/cinemas")
//public class CinemaController {
//
//    @Autowired
//    private CinemaService service;
//
//    @GetMapping
//    public List<CinemaResponseDTO> getAll() {
//        return service.getAll();
//    }
//
//    @PostMapping
//    @PreAuthorize("hasAuthority('ADMIN')")
//
//    public void create(@RequestBody CinemaRequestDTO dto) {
//        service.create(dto);
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('ADMIN')")
//
//    public void update(@PathVariable int id,
//                       @RequestBody CinemaRequestDTO dto) {
//        service.update(id, dto);
//    }
//}

package CinemaBooking.Group2.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.cinema.CinemaRequestDTO;
import CinemaBooking.Group2.dtos.cinema.CinemaResponseDTO;
import CinemaBooking.Group2.service.CinemaService;

@RestController
public class CinemaController {

    @Autowired
    private CinemaService service;

    // ================= PUBLIC GET =================
    // URL: /api/public/cinemas
    @GetMapping("/api/public/cinemas")
    public ResponseEntity<ApiResponse<List<CinemaResponseDTO>>> publicGetAll() {

        List<CinemaResponseDTO> data = service.getAll();

        return ResponseEntity.ok(
            new ApiResponse<>("Success", data)
        );
    }

    // ================= ADMIN APIs =================
    // URL: /api/cinemas
    @PostMapping("/api/cinemas")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<CinemaResponseDTO>> create(
            @RequestBody CinemaRequestDTO dto) {

        CinemaResponseDTO created = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Cinema created", created));
    }

    @PutMapping("/api/cinemas/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<CinemaResponseDTO>> update(
            @PathVariable int id,
            @RequestBody CinemaRequestDTO dto) {

        CinemaResponseDTO updated = service.update(id, dto);

        return ResponseEntity.ok(
                new ApiResponse<>("Cinema updated", updated)
        );
    }
}


