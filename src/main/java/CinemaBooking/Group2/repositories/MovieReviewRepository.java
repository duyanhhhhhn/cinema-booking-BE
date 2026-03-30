package CinemaBooking.Group2.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.movie_review.admin.MovieReviewDtos;
import CinemaBooking.Group2.dtos.movie_review.client.MovieReviewClientDtos;

@Repository
public class MovieReviewRepository {

    private final DataSource dataSource;

    public MovieReviewRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Điều kiện đủ để user được review:
     * - booking thuộc user
     * - booking gắn với showtime của đúng movie
     * - booking đã thanh toán thành công
     * - booking có thời điểm paid_at
     * - có ghế trong booking_seat
     * - showtime đã kết thúc
     */
    private static final String ELIGIBLE_REVIEW_SUBQUERY =
        "SELECT 1 " +
        "FROM booking b " +
        "JOIN showtime st ON st.id = b.showtime_id " +
        "WHERE b.user_id = ? " +
        "  AND st.movie_id = ? " +
        "  AND b.payment_status = 'PAID' " +
        "  AND b.paid_at IS NOT NULL " +
        "  AND EXISTS ( " +
        "    SELECT 1 " +
        "    FROM booking_seat bs " +
        "    WHERE bs.booking_id = b.id " +
        "  ) " +
        "  AND st.end_time IS NOT NULL " +
        "  AND st.end_time <= NOW() " +
        "LIMIT 1";

    public List<MovieReviewDtos> getAllReview() {
        String sql =
            "SELECT " +
            "  u.email, u.full_name, " +
            "  m.title, m.short_description, m.duration_minutes, m.genre, " +
            "  mr.id AS review_id, mr.rating, mr.comment, mr.created_at " +
            "FROM movie_review AS mr " +
            "JOIN movie AS m ON m.id = mr.movie_id " +
            "JOIN `user` AS u ON u.id = mr.user_id " +
            "ORDER BY mr.created_at DESC, mr.id DESC";

        List<MovieReviewDtos> movieReviews = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MovieReviewDtos dto = new MovieReviewDtos();
                dto.setEmail(rs.getString("email"));
                dto.setFull_name(rs.getString("full_name"));
                dto.setTitle(rs.getString("title"));
                dto.setShort_description(rs.getString("short_description"));
                dto.setDuration_minutes(rs.getInt("duration_minutes"));
                dto.setGenre(rs.getString("genre"));
                dto.setId(rs.getInt("review_id"));
                dto.setRating(rs.getString("rating"));
                dto.setComment(rs.getString("comment"));
                dto.setCreated_at(rs.getTimestamp("created_at"));
                movieReviews.add(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách movie review từ database role -> admin", e);
        }

        return movieReviews;
    }

    public long countByRoleId() {
        String sql =
            "SELECT COUNT(*) " +
            "FROM movie_review mr " +
            "JOIN `user` u ON u.id = mr.user_id " +
            "WHERE u.role_id = 2";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong(1) : 0L;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi count movie_review theo role_id=2", e);
        }
    }

    public List<MovieReviewClientDtos> getAllRating(int limit, int offset) {
        String sql =
            "SELECT mr.*, u.full_name, u.role_id " +
            "FROM movie_review AS mr " +
            "JOIN `user` AS u ON u.id = mr.user_id " +
            "WHERE u.role_id = 2 " +
            "  AND mr.is_hidden = 0 " +
            "ORDER BY mr.created_at DESC, mr.id DESC " +
            "LIMIT ? OFFSET ?";

        List<MovieReviewClientDtos> listMr = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovieReviewClientDtos dto = new MovieReviewClientDtos();
                    dto.setId(rs.getInt("id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setMovieId(rs.getInt("movie_id"));
                    dto.setRating(rs.getInt("rating"));
                    dto.setComment(rs.getString("comment"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setFull_name(rs.getString("full_name"));
                    dto.setRole_id(rs.getInt("role_id"));
                    listMr.add(dto);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách movie review từ database role -> users", e);
        }

        return listMr;
    }

    public float countRatingAverage(int movieId) {
        String sql =
            "SELECT COALESCE(ROUND(AVG(rating), 1), 0) AS avg_rating " +
            "FROM movie_review " +
            "WHERE movie_id = ? AND is_hidden = 0";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getFloat("avg_rating") : 0f;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "Lỗi khi tính trung bình cộng của review từ database -> kiểm tra lại query", e
            );
        }
    }

    public boolean existsByUserAndMovie(int userId, int movieId) {
        String sql =
            "SELECT EXISTS ( " +
            "  SELECT 1 " +
            "  FROM movie_review " +
            "  WHERE user_id = ? " +
            "    AND movie_id = ? " +
            "  LIMIT 1 " +
            ") AS existed";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, movieId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("existed");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra review đã tồn tại (user_id, movie_id)", e);
        }
    }

    public boolean canReview(int userId, int movieId) {
        String sql =
            "SELECT CASE " +
            "  WHEN EXISTS ( " + ELIGIBLE_REVIEW_SUBQUERY + " ) " +
            "   AND NOT EXISTS ( " +
            "     SELECT 1 " +
            "     FROM movie_review mr " +
            "     WHERE mr.user_id = ? " +
            "       AND mr.movie_id = ? " +
            "     LIMIT 1 " +
            "   ) " +
            "  THEN 1 ELSE 0 " +
            "END AS eligible";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;
            ps.setInt(i++, userId);
            ps.setInt(i++, movieId);
            ps.setInt(i++, userId);
            ps.setInt(i++, movieId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("eligible");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra điều kiện được review", e);
        }
    }

    public Integer createReviewAtomic(int userId, int movieId, int rating, String comment) {
        String sql =
            "INSERT INTO movie_review (user_id, movie_id, rating, comment) " +
            "SELECT ?, ?, ?, ? " +
            "FROM DUAL " +
            "WHERE ? BETWEEN 1 AND 5 " +
            "  AND NOT EXISTS ( " +
            "    SELECT 1 " +
            "    FROM movie_review mr " +
            "    WHERE mr.user_id = ? " +
            "      AND mr.movie_id = ? " +
            "    LIMIT 1 " +
            "  ) " +
            "  AND EXISTS ( " + ELIGIBLE_REVIEW_SUBQUERY + " )";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            ps.setInt(i++, userId);
            ps.setInt(i++, movieId);
            ps.setInt(i++, rating);
            ps.setString(i++, comment);

            ps.setInt(i++, rating);

            ps.setInt(i++, userId);
            ps.setInt(i++, movieId);

            ps.setInt(i++, userId);
            ps.setInt(i++, movieId);

            int rows = ps.executeUpdate();
            if (rows != 1) {
                return null;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return null;

        } catch (SQLIntegrityConstraintViolationException e) {
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tạo review (atomic insert)", e);
        }
    }

    public MovieReviewDtos findAdminDtoByReviewId(int reviewId) {
        String sql =
            "SELECT " +
            "  u.email, u.full_name, " +
            "  m.title, m.short_description, m.duration_minutes, m.genre, " +
            "  mr.id AS review_id, mr.rating, mr.comment, mr.created_at " +
            "FROM movie_review AS mr " +
            "JOIN movie AS m ON m.id = mr.movie_id " +
            "JOIN `user` AS u ON u.id = mr.user_id " +
            "WHERE mr.id = ? " +
            "LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reviewId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                MovieReviewDtos dto = new MovieReviewDtos();
                dto.setEmail(rs.getString("email"));
                dto.setFull_name(rs.getString("full_name"));
                dto.setTitle(rs.getString("title"));
                dto.setShort_description(rs.getString("short_description"));
                dto.setDuration_minutes(rs.getInt("duration_minutes"));
                dto.setGenre(rs.getString("genre"));
                dto.setId(rs.getInt("review_id"));
                dto.setRating(rs.getString("rating"));
                dto.setComment(rs.getString("comment"));
                dto.setCreated_at(rs.getTimestamp("created_at"));
                return dto;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy MovieReviewDtos theo review_id", e);
        }
    }

    public List<MovieReviewClientDtos> getAllCommnetByMovieId(int movieId, int limit, int offset) {
        if (limit < 1) limit = 20;
        if (offset < 0) offset = 0;

        String sql =
            "SELECT mr.id, mr.user_id, mr.movie_id, u.full_name, mr.rating, mr.comment, mr.created_at " +
            "FROM movie_review mr " +
            "JOIN `user` u ON mr.user_id = u.id " +
            "WHERE mr.movie_id = ? " +
            "  AND mr.is_hidden = 0 " +
            "ORDER BY mr.created_at DESC, mr.id DESC " +
            "LIMIT ? OFFSET ?";

        List<MovieReviewClientDtos> out = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, movieId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovieReviewClientDtos dto = new MovieReviewClientDtos();
                    dto.setId(rs.getInt("id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setMovieId(rs.getInt("movie_id"));
                    dto.setFull_name(rs.getString("full_name"));
                    dto.setRating(rs.getInt("rating"));
                    dto.setComment(rs.getString("comment"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    out.add(dto);
                }
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch reviews by movieId with pagination.", e);
        }
    }

    public long countAllCommentByMovieId(int movieId) {
        String sql =
            "SELECT COUNT(*) AS total " +
            "FROM movie_review " +
            "WHERE movie_id = ? " +
            "  AND is_hidden = 0";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("total") : 0L;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count reviews by movieId.", e);
        }
    }
}