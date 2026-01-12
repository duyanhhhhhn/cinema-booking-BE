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
import CinemaBooking.Group2.service.MovieService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<MovieDtos>>> getAllMovies() {
        return ResponseEntity.ok(new ApiResponse<>("Success", movieService.getAllMovie()));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllMovieStatus(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage) {

        page = Math.max(page, 1);
        perPage = Math.max(perPage, 1);

        List<MovieDtos> data = movieService.getAllMovieStatus(page, perPage);
        int total = movieService.countMovieStatus();

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", page);
        meta.put("perPage", perPage);
        meta.put("total", total);

        Map<String, Object> payload = new HashMap<>();
        payload.put("data", data);
        payload.put("meta", meta);

        return ResponseEntity.ok(new ApiResponse<>("Success", payload));
    }

    @GetMapping("/movie-detail/{id}")
    public ResponseEntity<ApiResponse<MovieDetailDtos>> getMovieDetailById(@PathVariable int id) {
        MovieDetailDtos data = movieService.getMovieDetailById(id);

        if (data == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Movie not found", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("Success", data));
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
    		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<MovieDetailDtos>> updateMovie(
            @PathVariable int id,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "poster", required = false) MultipartFile poster,
            @RequestPart(value = "banner", required = false) MultipartFile banner
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();	
            MovieEditDtos data = mapper.readValue(dataJson, MovieEditDtos.class);

            MovieDetailDtos updated = movieService.updateMovie(id, data, poster, banner);
            return ResponseEntity.ok(new ApiResponse<>("UPDATE MOVIE SUCCESS.", updated));

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("INVALID JSON FORMAT FOR FIELD 'data'.", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("UPDATE MOVIE FAILED.", null));
        }
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
