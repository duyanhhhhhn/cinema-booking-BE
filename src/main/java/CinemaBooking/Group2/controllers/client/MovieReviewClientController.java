package CinemaBooking.Group2.controllers.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import CinemaBooking.Group2.dtos.movie_review.client.MovieReviewClientDtos;
import CinemaBooking.Group2.dtos.movie_review.client.RatingSummaryDtos;
import CinemaBooking.Group2.models.PageResponse;
import CinemaBooking.Group2.service.MovieReviewService;

@Controller
@RequestMapping("/api/client/movie-reviews")
public class MovieReviewClientController {
	@Autowired
	MovieReviewService mv;
	
	@GetMapping({"", "/"})
	public ResponseEntity<PageResponse<MovieReviewClientDtos>> getAllReviews(
	        @RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "20") int perPage
	) {
	    if (page < 1) page = 1;
	    if (perPage < 1) perPage = 10;
	    if (perPage > 100) perPage = 100;

	    PageResponse<MovieReviewClientDtos> res = mv.getAllReviewClient(page, perPage);
	    return ResponseEntity.ok(res);
	}
	
	@GetMapping("/{movie_id}/rating")
	public ResponseEntity<RatingSummaryDtos> countRatingMovieById(
	        @PathVariable("movie_id") int movieId
	) {
	    RatingSummaryDtos res = mv.countRatingAverage(movieId);
	    return ResponseEntity.ok(res);
	}


}

