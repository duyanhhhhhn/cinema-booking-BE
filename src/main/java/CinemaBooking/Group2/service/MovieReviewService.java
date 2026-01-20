package CinemaBooking.Group2.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.movie_review.admin.MovieReviewDtos;
import CinemaBooking.Group2.dtos.movie_review.client.MovieReviewClientDtos;
import CinemaBooking.Group2.dtos.movie_review.client.RatingSummaryDtos;
import CinemaBooking.Group2.mappers.MovieReviewMapper;
import CinemaBooking.Group2.repositories.MovieReviewRepository;

@Service
public class MovieReviewService {
	@Autowired
	private MovieReviewRepository mvRepositories;
	

    public List<MovieReviewDtos> getAllReview() {
        try {
            return mvRepositories.getAllReview();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch movie review list. Please check repository/database.", e);
        }
    }
    
    public PageResponse<MovieReviewClientDtos> getAllReviewClient(int page, int size) {
        try {
            if (page < 1) page = 1;
            if (size < 1) size = 10;
            if (size > 100) size = 100;
            int offset = (page - 1) * size;
            long totalItems = mvRepositories.countByRoleId(); 
            List<MovieReviewClientDtos> items = mvRepositories.getAllRating(size, offset);
            PageResponse<MovieReviewClientDtos> res = new PageResponse<>();
            res.setItems(items);
            res.setPage(page);
            res.setSize(size);
            res.setTotalItems(totalItems);
            res.setTotalPages((long) Math.ceil(totalItems * 1.0 / size));
            res.setSuccess(true);
            res.setMessage("success");
            return res;
        } 
        catch (Exception e) {
            throw new RuntimeException("Failed to fetch paged movie review list. Please check repository/database.", e);
        }
    }
    
    // function help map repositorires count average rating services
    public RatingSummaryDtos countRatingAverage(int movie_id) {
    	try {
    		float avg = mvRepositories.countRatingAverage(movie_id);
    		return new RatingSummaryDtos(movie_id, avg);
    	}
    	catch (Exception e) {
            throw new RuntimeException("Lỗi khi tính trung bình tổng đánh giá hãy kiểm tra repo", e);
        }
    	
    }

    // Create review (atomic): PAID + SUCCESS + has seat + showtime ended + not reviewed.
    public MovieReviewDtos createReview(int userId, int movieId, int rating, String comment) {
        try {
            if (movieId <= 0) throw new IllegalArgumentException("movieId không hợp lệ");
            if (rating < 1 || rating > 5) throw new IllegalArgumentException("rating phải trong khoảng 1..5");
            if (comment != null && comment.length() > 2000) throw new IllegalArgumentException("comment tối đa 2000 ký tự");

            Integer newReviewId = mvRepositories.createReviewAtomic(userId, movieId, rating, comment);
            if (newReviewId != null) {
                MovieReviewDtos dto = mvRepositories.findAdminDtoByReviewId(newReviewId);
                if (dto != null) return dto;
                throw new RuntimeException("Tạo review thành công nhưng không lấy được dữ liệu trả về");
            }

            if (mvRepositories.existsByUserAndMovie(userId, movieId)) {
                throw new IllegalStateException("Bạn đã review phim này rồi");
            }

            if (!mvRepositories.canReview(userId, movieId)) {
                throw new IllegalStateException("Bạn chỉ có thể review sau khi đã mua vé và xem phim");
            }

            throw new IllegalStateException("Không thể tạo review. Vui lòng thử lại");

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create movie review. Please check repository/database.", e);
        }
    }
    
    public List<MovieReviewClientDtos> getAllCommentById(int movieId, int limit, int offset) {
        try {
            if (movieId <= 0) {
                throw new IllegalArgumentException("INVALID MOVIE ID.");
            }

            if (limit < 1) limit = 10;
            if (offset < 0) offset = 0;

            return mvRepositories.getAllCommnetByMovieId(movieId, limit, offset)
                    .stream()
                    .map(MovieReviewMapper::toDto)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) { 
            throw new RuntimeException(
                    "FAILED TO FETCH COMMENTS: movieId=" + movieId + ", limit=" + limit + ", offset=" + offset,
                    e
            );
        }
    }

}
