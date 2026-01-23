package CinemaBooking.Group2.controllers.admin;

import java.util.List;

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
public class CinemaController {

    @Autowired
    private CinemaService service;

    // ================= PUBLIC GET ================= Sử dụng để lấy danh sách rạp chiếu cho người dùng
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
    
    //    =====Upload ImageUrl======
    @PostMapping(
    	    value = "/api/cinemas/{id}/upload-image",
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    	    produces = MediaType.APPLICATION_JSON_VALUE
    	)
    	@PreAuthorize("hasRole('ADMIN')")
    	public ResponseEntity<ApiResponse<String>> uploadCinemaImage(
    	        @PathVariable int id,
    	        @ModelAttribute UploadImageRequestDTO dto
    	) {
    	    if (dto.getImage() == null || dto.getImage().isEmpty()) {
    	        return ResponseEntity.badRequest()
    	                .body(new ApiResponse<>("Image không được để trống", null));
    	    }

    	    String imageUrl = service.uploadCinemaImage(id, dto.getImage());

    	    return ResponseEntity.ok(
    	            new ApiResponse<>("Upload cinema image thành công", imageUrl)
    	    );
    	}


}


