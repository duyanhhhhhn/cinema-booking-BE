package CinemaBooking.Group2.controllers.client;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieDtos;
import CinemaBooking.Group2.service.MovieService;

@RestController
@RequestMapping("/api/public")
public class MoviePublicController {
	@Autowired
    private MovieService movieServices;
	
	@GetMapping("/movies")
	public ResponseEntity<ApiResponse<Map<String, Object>>> getAllMovieStatus(
	        @RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "10") int perPage) {

	    page = Math.max(page, 1);
	    perPage = Math.max(perPage, 1);

	    List<MovieDtos> data = movieServices.getAllMovieStatus(page, perPage);
	    long total = movieServices.countMovieStatus();

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
	    MovieDetailDtos data = movieServices.getMovieDetailById(id);

	    if (data == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(new ApiResponse<>("Movie not found", null));
	    }

	    return ResponseEntity.ok(new ApiResponse<>("Success", data));
	}


}
