package CinemaBooking.Group2.service;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.Meta;
import CinemaBooking.Group2.dtos.movie_review.admin.AdminReviewFilterDto;
import CinemaBooking.Group2.dtos.movie_review.admin.AdminReviewMovieOptionDto;
import CinemaBooking.Group2.dtos.movie_review.admin.AdminReviewRowDto;
import CinemaBooking.Group2.repositories.MovieReviewAdminRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MovieReviewAdminService {

    private final MovieReviewAdminRepository repo;

    public MovieReviewAdminService(MovieReviewAdminRepository repo) {
        this.repo = repo;
    }

    public ApiResponse<List<AdminReviewRowDto>> getAllReviews(AdminReviewFilterDto filter, int page, int perPage) {
        if (page <= 0) page = 1;
        if (perPage <= 0) perPage = 10;
        if (perPage > 50) perPage = 50;

        if (filter == null) {
            filter = new AdminReviewFilterDto();
        }

        long total = repo.countAllReviews(filter);
        List<AdminReviewRowDto> items = repo.findAllReviews(filter, page, perPage);

        Meta meta = new Meta(page, total, perPage);
        return new ApiResponse<>("OK", items, meta);
    }

    public ApiResponse<List<AdminReviewMovieOptionDto>> getMoviesHaveReviews() {
        List<AdminReviewMovieOptionDto> items = repo.findMoviesHaveReviews();
        Meta meta = new Meta(1, items.size(), items.size());
        return new ApiResponse<>("OK", items, meta);
    }

    public ApiResponse<Object> toggleHidden(int id) {
        Boolean hidden = repo.toggleHidden(id);
        if (hidden == null) return new ApiResponse<>("REVIEW_NOT_FOUND", null);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("hidden", hidden);

        return new ApiResponse<>("OK", data);
    }

    public ApiResponse<Object> setHidden(int id, boolean hidden) {
        boolean ok = repo.setHidden(id, hidden);
        if (!ok) return new ApiResponse<>("REVIEW_NOT_FOUND", null);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("hidden", hidden);

        return new ApiResponse<>("OK", data);
    }
}