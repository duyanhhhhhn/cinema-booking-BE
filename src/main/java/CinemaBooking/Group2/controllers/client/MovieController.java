package CinemaBooking.Group2.controllers.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.movie.MovieResponse;
import CinemaBooking.Group2.service.MovieService;

@RestController
@RequestMapping("/api/movie")
public class MovieController {
	@Autowired 
	private MovieService movieService;
	
	@GetMapping("/")
	public ResponseEntity<List<MovieResponse>> getAllMovies() {
		List<MovieResponse> movies = movieService.getAllMovie();
		return ResponseEntity.ok(movies);
	}
	
	@GetMapping("/test")
	public ResponseEntity<List<MovieResponse>> getAllMovieStatus() {
		List<MovieResponse> movies = movieService.getAllMovieStatus();
		return ResponseEntity.ok(movies);
	}
}
