package CinemaBooking.Group2.controllers.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.movie_review.admin.AdminReviewFilterDto;
import CinemaBooking.Group2.dtos.movie_review.admin.AdminReviewMovieOptionDto;
import CinemaBooking.Group2.dtos.movie_review.admin.AdminReviewRowDto;
import CinemaBooking.Group2.service.MovieReviewAdminService;

@RestController
@RequestMapping("/api/admin/movie-reviews")
public class MovieReviewAdminController {

    private final MovieReviewAdminService service;

    public MovieReviewAdminController(MovieReviewAdminService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminReviewRowDto>>> list(
            @RequestParam(required = false) Integer movieId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Boolean hidden,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        AdminReviewFilterDto filter = new AdminReviewFilterDto();
        filter.setMovieId(movieId);
        filter.setRating(rating);
        filter.setHidden(hidden);
        filter.setKeyword(keyword);

        return ResponseEntity.ok(service.getAllReviews(filter, page, perPage));
    }

    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<List<AdminReviewMovieOptionDto>>> movies() {
        return ResponseEntity.ok(service.getMoviesHaveReviews());
    }

    @PatchMapping("/{id}/toggle-hidden")
    public ResponseEntity<ApiResponse<Object>> toggleHidden(@PathVariable int id) {
        ApiResponse<Object> res = service.toggleHidden(id);
        if ("REVIEW_NOT_FOUND".equals(res.getMessage())) {
            return ResponseEntity.status(404).body(res);
        }
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{id}/hide")
    public ResponseEntity<ApiResponse<Object>> hide(@PathVariable int id) {
        ApiResponse<Object> res = service.setHidden(id, true);
        if ("REVIEW_NOT_FOUND".equals(res.getMessage())) {
            return ResponseEntity.status(404).body(res);
        }
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{id}/unhide")
    public ResponseEntity<ApiResponse<Object>> unhide(@PathVariable int id) {
        ApiResponse<Object> res = service.setHidden(id, false);
        if ("REVIEW_NOT_FOUND".equals(res.getMessage())) {
            return ResponseEntity.status(404).body(res);
        }
        return ResponseEntity.ok(res);
    }
}