package CinemaBooking.Group2.controllers.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieResponse;
import CinemaBooking.Group2.service.MovieService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    /**
     * Get all movies without any filtering or pagination.
     *
     * Response type:
     * - Returns a plain list of MovieResponse DTOs.
     *
     * When to use:
     * - Admin side or internal usage.
     * - Use carefully if the movie table can grow large.
     *
     * Endpoint:
     * GET /api/movie/
     *
     * @return HTTP 200 with list of movies
     */
    @GetMapping("/")
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        List<MovieResponse> movies = movieService.getAllMovie();
        return ResponseEntity.ok(movies);
    }

    /**
     * Get public movies with pagination and response metadata.
     *
     * The response format is wrapped to include:
     * - message: indicates operation result
     * - data: actual movie list
     * - meta: pagination information (page, total, perPage)
     *
     * Query params:
     * - page: page number starting from 1 (default 1)
     * - perPage: number of records per page (default 10)
     *
     * Validation:
     * - If page < 1, fallback to 1
     * - If perPage < 1, fallback to 10
     *
     * Endpoint:
     * GET /api/movie/public?page=1&perPage=10
     *
     * @param page current page number (1-based index)
     * @param perPage number of items per page
     * @return HTTP 200 with message, data, and meta
     */
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
    
    /**
     * Get movie detail by id.
     *
     * Response format:
     * - message: "Success" if found, otherwise "Not Found"
     * - data: MovieDetailDtos (or null if not found)
     *
     * Status codes:
     * - 200 OK when movie exists
     * - 404 Not Found when movie does not exist
     *
     * Endpoint example:
     * GET /api/movie/4
     *
     * @param id movie id from URL path
     * @return JSON response with message and data
     */
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

}
