package CinemaBooking.Group2.controllers.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.cinema.CinemaRequestDTO;
import CinemaBooking.Group2.dtos.cinema.CinemaResponseDTO;
import CinemaBooking.Group2.dtos.cinema.UploadImageRequestDTO;
import CinemaBooking.Group2.service.CinemaService;

@RestController
@RequestMapping("/api")
public class CinemaController {

    @Autowired
    private CinemaService cinemaService;

    // ================= ADMIN GET ALL =================
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/cinemas")
    public ResponseEntity<ApiResponse<List<CinemaResponseDTO>>> getAllCinemasForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(required = false) String search
    ) {

        page = Math.max(page, 1);
        perPage = Math.max(perPage, 1);

        List<CinemaResponseDTO> data =
                cinemaService.getAllPagedIncludingInactiveWithSearch(page, perPage, search);

        long total =
                cinemaService.countAllCinemasIncludingInactiveWithSearch(search);

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", page);
        meta.put("perPage", perPage);
        meta.put("total", total);

        return ResponseEntity.ok(
                new ApiResponse<>("Success", data, meta)
        );
    }


    // ================= PUBLIC GET =================
    @GetMapping("/public/cinemas")
    public ResponseEntity<ApiResponse<List<CinemaResponseDTO>>> getPublicCinemas(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int perPage,
            @RequestParam(required = false) String search
    ) {

        page = Math.max(page, 1);
        perPage = Math.max(perPage, 1);

        List<CinemaResponseDTO> data =
                cinemaService.getAllPagedWithSearch(page, perPage, search);

        long total =
                cinemaService.countAllActiveCinemasWithSearch(search);

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", page);
        meta.put("perPage", perPage);
        meta.put("total", total);

        return ResponseEntity.ok(
                new ApiResponse<>("Success", data, meta)
        );
    }


    // ================= CREATE =================
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(
            value = "/cinemas",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<CinemaResponseDTO>> createCinema(
            @ModelAttribute CinemaRequestDTO dto) {

        CinemaResponseDTO created =
                cinemaService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Cinema created", created));
    }


    // ================= UPDATE =================
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(
            value = "/cinemas/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<CinemaResponseDTO>> updateCinema(
            @PathVariable int id,
            @ModelAttribute CinemaRequestDTO dto) {

        CinemaResponseDTO updated =
                cinemaService.update(id, dto);

        return ResponseEntity.ok(
                new ApiResponse<>("Cinema updated", updated)
        );
    }


    // ================= UPLOAD IMAGE =================
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(
            value = "/cinemas/{id}/upload-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<String>> uploadCinemaImage(
            @PathVariable int id,
            @ModelAttribute UploadImageRequestDTO dto) {

        if (dto.getImage() == null || dto.getImage().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Image không được để trống", null));
        }

        String imageUrl =
                cinemaService.uploadCinemaImage(id, dto.getImage());

        return ResponseEntity.ok(
                new ApiResponse<>("Upload cinema image thành công", imageUrl)
        );
    }


    // ================= DELETE =================
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/cinemas/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCinema(
            @PathVariable int id) {

        cinemaService.deactivate(id);

        return ResponseEntity.ok(
                new ApiResponse<>("Cinema deactivated", "OK")
        );
    }
    
    // ================= ACTIVATE =================
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/cinemas/{id}/activate")
    public ResponseEntity<ApiResponse<String>> activateCinema(@PathVariable int id) {
        cinemaService.activate(id);
        return ResponseEntity.ok(new ApiResponse<>("Cinema activated", "OK"));
    }
}
