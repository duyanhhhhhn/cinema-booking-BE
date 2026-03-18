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
import CinemaBooking.Group2.dtos.movie.MovieCardtos;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MoviePublicDtos;
import CinemaBooking.Group2.dtos.movie.MovieWithShowtimesDtos;
import CinemaBooking.Group2.dtos.movie.RelatedMovieItemDtos;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.service.MovieService;
import CinemaBooking.Group2.service.ShowtimeService;

@RestController
@RequestMapping("/api/public")
public class MoviePublicController {
	@Autowired
    private MovieService movieServices;
	
	@Autowired
	private ShowtimeService showtimeServices;
	
	@GetMapping("/movies")
	public ResponseEntity<ApiResponse<List<MoviePublicDtos>>> getAllMovieStatus(
			@RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "15")  int perPage,
	        @RequestParam(required = false) String title,
	        @RequestParam(required = false) Movie.MovieGenre genre,
	        @RequestParam(required = false) Movie.MovieStatus status
	) {
	    page = Math.max(page, 1);
	    perPage = Math.max(perPage, 1);

	    List<MoviePublicDtos> data =
	            movieServices.getAllMovieCommingSoon(page, perPage, title, genre, status);

	    long total =
	            movieServices.countMovieComingSoonNowShowing(title, genre, status);

	    Map<String, Object> meta = new HashMap<>();
	    meta.put("page", page);
	    meta.put("perPage", perPage);
	    meta.put("total", total);

	    return ResponseEntity.ok(new ApiResponse<>("Success", data, meta));
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

    @GetMapping("/{movieId}/cinemas-showtimes")
    public ResponseEntity<List<MovieWithShowtimesDtos>> getCinemasWithShowtimesByMovieId(
            @PathVariable int movieId,
            @RequestParam(required = false) Integer cinemaId
    ) {
        List<MovieWithShowtimesDtos> data = showtimeServices.getCinemasWithShowtimesByMovieId(movieId, cinemaId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/movies/related")
    public ResponseEntity<List<RelatedMovieItemDtos>> getRelatedMovies(
            @RequestParam String genre,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(movieServices.getRelatedMovies(genre, limit));
    }


}