package CinemaBooking.Group2.controllers.client;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import CinemaBooking.Group2.dtos.movie_review.client.MovieReviewClientDtos;
import CinemaBooking.Group2.service.MovieReviewService;

@Controller
@RequestMapping("/api/client/movie-reviews")
public class MovieReviewClientController {
	@Autowired
	MovieReviewService mv;
	
	@GetMapping({"", "/"})
    public ResponseEntity<List<MovieReviewClientDtos>> getAllReviews() {
        List<MovieReviewClientDtos> reviews = mv.getAllReviewClient();
        return ResponseEntity.ok(reviews);
    }
}
