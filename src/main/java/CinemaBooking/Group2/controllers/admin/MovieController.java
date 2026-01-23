package CinemaBooking.Group2.controllers.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.movie.MovieCreateDtos;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieDtos;
import CinemaBooking.Group2.dtos.movie.MovieEditDtos;
import CinemaBooking.Group2.dtos.movie.MoviePublicDtos;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.Movie.MovieGenre;
import CinemaBooking.Group2.service.MovieService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<MoviePublicDtos>>> getAllMovie(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Movie.MovieGenre genre) {

        // 1. Logic tính toán
        page = Math.max(page, 1);
        perPage = Math.max(perPage, 1);

        List<MoviePublicDtos> movies = movieService.getAllMovieCommingSoon(page, perPage, title, genre);
        long total = movieService.countTotalMovies();

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", page);
        meta.put("perPage", perPage);
        meta.put("total", total);

        return ResponseEntity.ok(new ApiResponse<>("Success", movies, meta));
    }
    
    
    @PostMapping(value = "/create-movies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<MovieDetailDtos>> createMovie(@ModelAttribute MovieCreateDtos dto) {
        if (dto.getPosterFile() == null || dto.getPosterFile().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Poster is required", null));
        }
        if (dto.getBannerFile() == null || dto.getBannerFile().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Banner is required", null));
        }

        MovieDetailDtos created = movieService.createMovie(dto, dto.getPosterFile(), dto.getBannerFile());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Created", created));
    }
    
    
    @PutMapping(
            value = "/edit-movie/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<MovieDetailDtos>> updateMovie(
            @PathVariable("id") Integer id,
            @ModelAttribute MovieEditDtos dto
    ) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>("INVALID MOVIE ID.", null));
            }

            // FILE CÓ THỂ NULL (KHÔNG ĐỔI ẢNH THÌ BỎ QUA)
            MultipartFile poster = normalizeOptionalFile(dto.getPosterFile());
            MultipartFile banner = normalizeOptionalFile(dto.getBannerFile());

            MovieDetailDtos updated = movieService.updateMovie(id, dto, poster, banner);
            return ResponseEntity.ok(new ApiResponse<>("UPDATE MOVIE SUCCESS.", updated));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("UPDATE MOVIE FAILED.", null));
        }
    }

    // COI FILE RỖNG LÀ NULL ĐỂ TRÁNH XỬ LÝ UPDATE ẢNH
    private MultipartFile normalizeOptionalFile(MultipartFile f) {
        if (f == null) return null;
        if (f.isEmpty() || f.getSize() <= 0) return null;
        if (f.getOriginalFilename() == null || f.getOriginalFilename().isBlank()) return null;
        return f;
    }



    

    @DeleteMapping("/delete-movies/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteMovie(@PathVariable int id) {
        try {
            boolean ok = movieService.deleteMovie(id);
            if (!ok) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("Delete movie failed", false));
            }

            return ResponseEntity.ok(new ApiResponse<>("Deleted", true));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Server error", false));
        }
    }
}
