package CinemaBooking.Group2.service;

import java.util.List;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.movie_review.admin.MovieReviewDtos;
import CinemaBooking.Group2.dtos.movie_review.client.MovieReviewClientDtos;
import CinemaBooking.Group2.dtos.movie_review.client.RatingSummaryDtos;
import CinemaBooking.Group2.repositories.MovieReviewRepository;

@Service
public class MovieReviewService {

    private final MovieReviewRepository movieReviewRepository;

    public MovieReviewService(MovieReviewRepository movieReviewRepository) {
        this.movieReviewRepository = movieReviewRepository;
    }

    /**
     * Lấy toàn bộ review cho admin.
     */
    public List<MovieReviewDtos> getAllReview() {
        try {
            return movieReviewRepository.getAllReview();
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to fetch movie review list. Please check repository/database.",
                e
            );
        }
    }

    /**
     * Lấy review public cho client có phân trang.
     */
    public List<MovieReviewClientDtos> getAllReviewClient(int page, int size) {
        try {
            if (page < 1) page = 1;
            if (size < 1) size = 10;
            if (size > 100) size = 100;

            int offset = (page - 1) * size;
            return movieReviewRepository.getAllRating(size, offset);

        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to fetch paged movie review list. Please check repository/database.",
                e
            );
        }
    }

    /**
     * Tính rating trung bình của một movie.
     */
    public RatingSummaryDtos countRatingAverage(int movieId) {
        try {
            if (movieId <= 0) {
                throw new IllegalArgumentException("INVALID MOVIE ID.");
            }

            float avg = movieReviewRepository.countRatingAverage(movieId);
            return new RatingSummaryDtos(movieId, avg);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                "Lỗi khi tính trung bình tổng đánh giá, hãy kiểm tra repository.",
                e
            );
        }
    }

    /**
     * Check user có đủ điều kiện review hay không.
     *
     * Điều kiện hợp lệ:
     * - user chưa review phim này
     * - đã có booking cho đúng movie
     * - có ghế trong booking
     * - booking đã PAID hoặc payment SUCCESS
     * - showtime của booking đã kết thúc
     */
    public boolean canReview(int userId, int movieId) {
        try {
            if (userId <= 0) {
                throw new IllegalArgumentException("INVALID USER ID.");
            }
            if (movieId <= 0) {
                throw new IllegalArgumentException("INVALID MOVIE ID.");
            }

            return movieReviewRepository.canReview(userId, movieId);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to check review eligibility: userId=" + userId + ", movieId=" + movieId,
                e
            );
        }
    }

    /**
     * Tạo review mới.
     *
     * Rule:
     * - rating phải từ 1..5
     * - user chưa review movie này
     * - user phải có booking hợp lệ cho movie đó
     * - showtime của booking phải đã kết thúc
     */
    public MovieReviewDtos createReview(int userId, int movieId, int rating, String comment) {
        try {
            if (userId <= 0) {
                throw new IllegalArgumentException("INVALID USER ID.");
            }
            if (movieId <= 0) {
                throw new IllegalArgumentException("INVALID MOVIE ID.");
            }
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("RATING MUST BE BETWEEN 1 AND 5.");
            }

            String normalizedComment = comment == null ? "" : comment.trim();
            if (normalizedComment.length() > 2000) {
                throw new IllegalArgumentException("COMMENT MUST NOT EXCEED 2000 CHARACTERS.");
            }

            Integer newReviewId = movieReviewRepository.createReviewAtomic(
                userId,
                movieId,
                rating,
                normalizedComment
            );

            if (newReviewId != null) {
                MovieReviewDtos dto = movieReviewRepository.findAdminDtoByReviewId(newReviewId);
                if (dto != null) {
                    return dto;
                }
                throw new RuntimeException("Tạo đánh giá thành công nhưng không lấy được dữ liệu trả về.");
            }

            if (movieReviewRepository.existsByUserAndMovie(userId, movieId)) {
                throw new IllegalStateException("Bạn đã đánh giá phim này rồi.");
            }

            if (!movieReviewRepository.canReview(userId, movieId)) {
                throw new IllegalStateException(
                    "Bạn chỉ có thể đánh giá sau khi đã mua vé, thanh toán thành công và suất chiếu đã kết thúc."
                );
            }

            throw new IllegalStateException("Không thể tạo đánh giá. Vui lòng thử lại.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to create movie review. Please check repository/database.",
                e
            );
        }
    }

    /**
     * Lấy comment theo movieId có phân trang.
     */
    public List<MovieReviewClientDtos> getAllCommentById(int movieId, int limit, int offset) {
        try {
            if (movieId <= 0) {
                throw new IllegalArgumentException("INVALID MOVIE ID.");
            }
            if (limit < 1) limit = 20;
            if (offset < 0) offset = 0;

            return movieReviewRepository.getAllCommnetByMovieId(movieId, limit, offset);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                "FAILED TO FETCH COMMENTS: movieId=" + movieId + ", limit=" + limit + ", offset=" + offset,
                e
            );
        }
    }

    /**
     * Đếm tổng số comment đang hiển thị của movie.
     */
    public long countAllCommentByMovieId(int movieId) {
        try {
            if (movieId <= 0) {
                throw new IllegalArgumentException("INVALID MOVIE ID.");
            }

            return movieReviewRepository.countAllCommentByMovieId(movieId);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO COUNT COMMENTS: movieId=" + movieId, e);
        }
    }
}