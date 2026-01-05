package CinemaBooking.Group2.controllers.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.movie.MovieCreateDtos;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieEditDtos;
import CinemaBooking.Group2.dtos.movie.MovieResponse;
import CinemaBooking.Group2.service.MovieService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("")
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        List<MovieResponse> movies = movieService.getAllMovie();
        return ResponseEntity.ok(movies);
    }
    
    // get all movie if status = "Comming soon"
    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getAllMovieStatus(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        if (page < 1) page = 1;
        if (perPage < 1) perPage = 10;

        List<MovieResponse> data = movieService.getAllMovieStatus(page, perPage);
        int total = movieService.countMovieStatus();

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", page);
        meta.put("total", total);
        meta.put("perPage", perPage);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Success");
        res.put("data", data);
        res.put("meta", meta);

        return ResponseEntity.ok(res);
    }
    
    // get movie detail by id when user click see detail
    @GetMapping("/movie-detail/{id}")
    public ResponseEntity<Map<String, Object>> getMovieDetailById(@PathVariable int id) {
        MovieDetailDtos data = movieService.getMovieDetailById(id);

        Map<String, Object> res = new HashMap<>();

        if (data == null) {
            res.put("message", "Movie not found");
            res.put("data", null);
            return ResponseEntity.status(404).body(res);
        }

        res.put("message", "Success");
        res.put("data", data);
        return ResponseEntity.ok(res);
    }
 
    // create new film
    @PostMapping("/create-movies")
    public ResponseEntity<Boolean> createNewMovie(@RequestBody MovieCreateDtos dto) {
        try {
            boolean ok = movieService.createNewMovie(dto);
            if (ok) {
                return ResponseEntity.status(HttpStatus.CREATED).body(true);
            }
            return ResponseEntity.badRequest().body(false);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
    
    // delete movie by id get on link 
    @DeleteMapping("/delete-movies/{id}")
    public ResponseEntity<Boolean> deleteMovie(@PathVariable int id) {
    	try {
    		boolean ok = movieService.deleteMovie(id);
    		if (ok) return ResponseEntity.status(HttpStatus.CREATED).body(true);
    		return ResponseEntity.badRequest().body(false);
    	} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
    
    @PutMapping("/edit-movies/{id}")
    public ResponseEntity<?> updateMovie(
            @PathVariable int id,
            @RequestBody MovieEditDtos dto
    ) {
        try {
            MovieDetailDtos updated = movieService.updateMovie(id, dto);
            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            // tuỳ bạn: phân loại message để trả 404/400
            String msg = e.getMessage();

            if (msg != null && msg.toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }
}
