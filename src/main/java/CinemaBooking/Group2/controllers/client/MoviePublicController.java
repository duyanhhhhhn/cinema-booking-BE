package CinemaBooking.Group2.controllers.client;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.movie.MovieCardtos;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieDtos;
import CinemaBooking.Group2.dtos.movie.MoviePublicDtos;
import CinemaBooking.Group2.dtos.movie.MovieWithShowtimesDtos;
import CinemaBooking.Group2.dtos.showtime.CinemaOptionsDtos;
import CinemaBooking.Group2.mappers.MovieMapper;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.service.MovieService;

@RestController
@RequestMapping("/api/public")
public class MoviePublicController {
	@Autowired
    private MovieService movieServices;
	
	@GetMapping("/cinemas/{cinemaId}/movies")
	public ResponseEntity<List<MovieWithShowtimesDtos>> getMoviesWithShowtimesByCinema(
	        @PathVariable int cinemaId,
	        @RequestParam(required = false) String title,
	        @RequestParam(required = false) Movie.MovieGenre genre
	) {
	    return ResponseEntity.ok(movieServices.getMoviesWithShowtimesByCinema(cinemaId, title, genre));
	}
	
	@GetMapping("/cinema-movies")
	public ResponseEntity<List<CinemaOptionsDtos>> getCinemaOptions() {
	    return ResponseEntity.ok(movieServices.getCinemaOptions());
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
	
    @GetMapping("/movies/status")
    public ResponseEntity<ApiResponse<List<MovieCardtos>>> getMoviesComingSoonAndNowShowing() {
        List<MovieCardtos> movies = movieServices.getMoviesComingSoonAndNowShowing();
        return ResponseEntity.ok(new ApiResponse<>("Success", movies));
    }


}
