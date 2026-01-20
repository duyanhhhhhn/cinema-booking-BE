package CinemaBooking.Group2.controllers.client;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
// ADDED
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.ApiResponse; // ADDED
import CinemaBooking.Group2.dtos.movie_review.admin.MovieReviewDtos; // ADDED
import CinemaBooking.Group2.dtos.movie_review.client.MovieCreateReviewDtos;
import CinemaBooking.Group2.dtos.movie_review.client.MovieReviewClientDtos;
import CinemaBooking.Group2.dtos.movie_review.client.RatingSummaryDtos;
import CinemaBooking.Group2.service.MovieReviewService;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/client/reviews")
public class MovieReviewClientController {
	@Autowired
	MovieReviewService mv;
	
	@GetMapping({"", "/"})
	public ResponseEntity<ApiResponse<List<MovieReviewClientDtos>>> getAllReviews(
	        @RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "20") int perPage
	) {
	    List<MovieReviewClientDtos> items =
	    		mv.getAllReviewClient(page, perPage);

	    Map<String, Object> meta = new LinkedHashMap<>();
	    meta.put("page", page);
	    meta.put("perPage", perPage);
	    meta.put("total", items.size()); 

	    return ResponseEntity.ok(
	        new ApiResponse<>("OK", items, meta)
	    );
	}

	
	@GetMapping("/{movie_id}/rating")
	public ResponseEntity<ApiResponse<RatingSummaryDtos>> countRatingMovieById(
	        @PathVariable("movie_id") int movieId
	) {
	    if (movieId <= 0) {
	        return ResponseEntity.badRequest()
	            .body(new ApiResponse<>("INVALID MOVIE ID.", null, null));
	    }

	    RatingSummaryDtos data = mv.countRatingAverage(movieId);

	    return ResponseEntity.ok(
	        new ApiResponse<>("success", data, null)
	    );
	}


	// đánh giá comment của khách hàng với điều kiện, người dùng đã mua vé.
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
		System.out.println("HIT create-comment: userId=" + userId + ", movieId=" + movieId);
		    try {
		        if (userId <= 0) {
		            return ResponseEntity.status(401).body(new ApiResponse<>("UNAUTHORIZED: userId invalid.", null));
		        }

		        MovieReviewDtos dto = mv.createReview(userId, movieId, rating, comment);
		        return ResponseEntity.status(201).body(new ApiResponse<>("CREATE REVIEW SUCCESS.", dto));

		    } catch (IllegalArgumentException e) {
		        return ResponseEntity.status(400).body(new ApiResponse<>(e.getMessage(), null));

		    } catch (IllegalStateException e) {
		        String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
		        if (msg.contains("đã review") || msg.contains("da review")) {
		            return ResponseEntity.status(409).body(new ApiResponse<>(e.getMessage(), null));
		        }
		        return ResponseEntity.status(403).body(new ApiResponse<>(e.getMessage(), null));

		    } catch (Exception e) {
		        return ResponseEntity.status(500).body(new ApiResponse<>("SERVER ERROR.", null));
		    }
		}
    
		@GetMapping("/{movie_id}/comment")
		public ResponseEntity<ApiResponse<List<MovieReviewClientDtos>>> getAllComment(
		        @PathVariable("movie_id") int movieId,
		        @RequestParam(defaultValue = "1") int page,
		        @RequestParam(defaultValue = "20") int perPage
		) {
		    if (movieId <= 0) {
		        throw new IllegalArgumentException("INVALID MOVIE ID.");
		    }
	
		    if (page < 1) page = 1;
		    if (perPage < 1) perPage = 10;
		    if (perPage > 100) perPage = 100;
	
		    int limit = perPage;
		    int offset = (page - 1) * perPage;
	
		    List<MovieReviewClientDtos> data =
		            mv.getAllCommentById(movieId, limit, offset);
		    long total = 0L;
	
		    Map<String, Object> meta = new LinkedHashMap<>();
		    meta.put("page", page);
		    meta.put("perPage", perPage);
		    meta.put("total", total);
	
		    return ResponseEntity.ok(
		            new ApiResponse<>("success", data, meta)
		    );
		}


}
