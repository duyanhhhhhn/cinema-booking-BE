package CinemaBooking.Group2.controllers.admin;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import CinemaBooking.Group2.dtos.movie_review.admin.MovieReviewDtos;
import CinemaBooking.Group2.service.MovieReviewService;

@Controller
@RequestMapping("/api/admin/movie-reviews")
public class MovieReviewController {

    private final MovieReviewService movieReviewService;

    public MovieReviewController(MovieReviewService movieReviewService) {
        this.movieReviewService = movieReviewService;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<List<MovieReviewDtos>> getAllReviews() {
        List<MovieReviewDtos> reviews = movieReviewService.getAllReview();
        return ResponseEntity.ok(reviews);
    }
}