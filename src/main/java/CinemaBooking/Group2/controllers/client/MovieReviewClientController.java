package CinemaBooking.Group2.controllers.client;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.movie_review.admin.MovieReviewDtos;
import CinemaBooking.Group2.dtos.movie_review.client.MovieReviewClientDtos;
import CinemaBooking.Group2.dtos.movie_review.client.RatingSummaryDtos;
import CinemaBooking.Group2.service.MovieReviewService;

@RestController
@RequestMapping("/api/client/reviews")
public class MovieReviewClientController {

    private final MovieReviewService movieReviewService;

    public MovieReviewClientController(MovieReviewService movieReviewService) {
        this.movieReviewService = movieReviewService;
    }

    /**
     * Lấy danh sách review public cho client.
     */
    @GetMapping({"", "/"})
    public ResponseEntity<ApiResponse<List<MovieReviewClientDtos>>> getAllReviews(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int perPage
    ) {
        try {
            if (page < 1) page = 1;
            if (perPage < 1) perPage = 20;
            if (perPage > 100) perPage = 100;

            List<MovieReviewClientDtos> items = movieReviewService.getAllReviewClient(page, perPage);

            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("page", page);
            meta.put("perPage", perPage);
            meta.put("total", items.size());

            return ResponseEntity.ok(new ApiResponse<>("OK", items, meta));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse<>("SERVER ERROR.", null, null)
            );
        }
    }

    /**
     * Lấy rating trung bình của movie.
     */
    @GetMapping("/{movie_id}/rating")
    public ResponseEntity<ApiResponse<RatingSummaryDtos>> countRatingMovieById(
        @PathVariable("movie_id") int movieId
    ) {
        try {
            RatingSummaryDtos data = movieReviewService.countRatingAverage(movieId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", data, null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(e.getMessage(), null, null));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>("SERVER ERROR.", null, null));
        }
    }

    /**
     * Check user có đủ điều kiện đánh giá phim hay không.
     */
    @GetMapping("/{movie_id}/can-review")
    public ResponseEntity<ApiResponse<Map<String, Object>>> canReview(
        @PathVariable("movie_id") int movieId,
        @RequestParam int userId
    ) {
        try {
            boolean canReview = movieReviewService.canReview(userId, movieId);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("userId", userId);
            data.put("movieId", movieId);
            data.put("canReview", canReview);

            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", data, null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(e.getMessage(), null, null));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>("SERVER ERROR.", null, null));
        }
    }

    /**
     * Tạo comment / review cho movie.
     */
    @PostMapping(
        value = "/create-comment",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<MovieReviewDtos>> createComment(
        @RequestParam Integer userId,
        @RequestParam Integer movieId,
        @RequestParam Integer rating,
        @RequestParam(required = false, defaultValue = "") String comment
    ) {
        try {
            MovieReviewDtos dto = movieReviewService.createReview(userId, movieId, rating, comment);
            return ResponseEntity.status(201)
                .body(new ApiResponse<>("CREATE REVIEW SUCCESS.", dto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(e.getMessage(), null));

        } catch (IllegalStateException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

            if (msg.contains("đã đánh giá")
                || msg.contains("da danh gia")
                || msg.contains("đã review")
                || msg.contains("da review")) {
                return ResponseEntity.status(409)
                    .body(new ApiResponse<>(e.getMessage(), null));
            }

            return ResponseEntity.status(403)
                .body(new ApiResponse<>(e.getMessage(), null));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>("SERVER ERROR.", null));
        }
    }

    /**
     * Lấy danh sách comment theo movieId có phân trang.
     */
    @GetMapping("/{movie_id}/comment")
    public ResponseEntity<ApiResponse<List<MovieReviewClientDtos>>> getAllComment(
        @PathVariable("movie_id") int movieId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int perPage
    ) {
        try {
            if (page < 1) page = 1;
            if (perPage < 1) perPage = 20;
            if (perPage > 100) perPage = 100;

            int limit = perPage;
            int offset = (page - 1) * perPage;

            List<MovieReviewClientDtos> data =
                movieReviewService.getAllCommentById(movieId, limit, offset);

            long total = movieReviewService.countAllCommentByMovieId(movieId);
            long totalPages = total == 0 ? 0 : (long) Math.ceil((double) total / perPage);

            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("page", page);
            meta.put("perPage", perPage);
            meta.put("total", total);
            meta.put("totalPages", totalPages);

            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", data, meta));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(e.getMessage(), null, null));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>("SERVER ERROR.", null, null));
        }
    }
}