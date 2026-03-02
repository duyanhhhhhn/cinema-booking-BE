package CinemaBooking.Group2.repositories;

import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.movie_review.admin.AdminReviewFilterDto;
import CinemaBooking.Group2.dtos.movie_review.admin.AdminReviewMovieOptionDto;
import CinemaBooking.Group2.dtos.movie_review.admin.AdminReviewRowDto;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MovieReviewAdminRepository {

    private final DataSource dataSource;

    public MovieReviewAdminRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<AdminReviewRowDto> findAllReviews(AdminReviewFilterDto filter, int page, int size) {
        if (page < 1) page = 1;
        if (size <= 0) size = 10;

        int offset = (page - 1) * size;
        Integer movieId = (filter != null) ? filter.getMovieId() : null;

        final String sqlAll =
                "SELECT " +
                "  r.id AS id, " +
                "  r.user_id AS user_id, " +
                "  u.full_name AS full_name, " +
                "  u.email AS email, " +
                "  r.movie_id AS movie_id, " +
                "  m.title AS movie_title, " +
                "  r.rating AS rating, " +
                "  r.comment AS comment, " +
                "  r.is_hidden AS is_hidden, " +
                "  r.created_at AS created_at " +
                "FROM movie_review AS r " +
                "JOIN movie AS m ON m.id = r.movie_id " +
                "JOIN user AS u ON u.id = r.user_id " +
                "ORDER BY r.created_at DESC, r.id DESC " +
                "LIMIT ? OFFSET ?";

        final String sqlByMovie =
                "SELECT " +
                "  r.id AS id, " +
                "  r.user_id AS user_id, " +
                "  u.full_name AS full_name, " +
                "  u.email AS email, " +
                "  r.movie_id AS movie_id, " +
                "  m.title AS movie_title, " +
                "  r.rating AS rating, " +
                "  r.comment AS comment, " +
                "  r.is_hidden AS is_hidden, " +
                "  r.created_at AS created_at " +
                "FROM movie_review AS r " +
                "JOIN movie AS m ON m.id = r.movie_id " +
                "JOIN user AS u ON u.id = r.user_id " +
                "WHERE r.movie_id = ? " +
                "ORDER BY r.created_at DESC, r.id DESC " +
                "LIMIT ? OFFSET ?";

        List<AdminReviewRowDto> out = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            if (movieId == null) {
                try (PreparedStatement ps = conn.prepareStatement(sqlAll)) {
                    ps.setInt(1, size);
                    ps.setInt(2, offset);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) out.add(mapRowSetterStyle(rs));
                    }
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(sqlByMovie)) {
                    ps.setInt(1, movieId);
                    ps.setInt(2, size);
                    ps.setInt(3, offset);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) out.add(mapRowSetterStyle(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("MovieReviewAdminRepository.findAllReviews failed: " + e.getMessage(), e);
        }

        return out;
    }

    private AdminReviewRowDto mapRowSetterStyle(ResultSet rs) throws SQLException {
        AdminReviewRowDto dto = new AdminReviewRowDto();

        dto.setId(rs.getInt("id"));
        dto.setUserId(rs.getInt("user_id"));
        dto.setUserFullName(rs.getString("full_name"));
        dto.setUserEmail(rs.getString("email"));
        dto.setMovieId(rs.getInt("movie_id"));
        dto.setMovieTitle(rs.getString("movie_title"));
        dto.setRating(rs.getInt("rating"));
        dto.setComment(rs.getString("comment"));

        Timestamp ts = rs.getTimestamp("created_at");
        dto.setCreatedAt(ts == null ? null : ts.toLocalDateTime());

        dto.setHidden(rs.getBoolean("is_hidden"));
        return dto;
    }

    public long countAllReviews(AdminReviewFilterDto filter) {
        Integer movieId = (filter != null) ? filter.getMovieId() : null;

        final String sqlAll =
                "SELECT COUNT(*) AS total " +
                "FROM movie_review r " +
                "JOIN user u ON u.id = r.user_id " +
                "JOIN movie m ON m.id = r.movie_id";

        final String sqlByMovie =
                "SELECT COUNT(*) AS total " +
                "FROM movie_review r " +
                "JOIN user u ON u.id = r.user_id " +
                "JOIN movie m ON m.id = r.movie_id " +
                "WHERE r.movie_id = ?";

        try (Connection cn = dataSource.getConnection()) {
            if (movieId == null) {
                try (PreparedStatement ps = cn.prepareStatement(sqlAll);
                     ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getLong("total") : 0L;
                }
            } else {
                try (PreparedStatement ps = cn.prepareStatement(sqlByMovie)) {
                    ps.setInt(1, movieId);
                    try (ResultSet rs = ps.executeQuery()) {
                        return rs.next() ? rs.getLong("total") : 0L;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("MovieReviewAdminRepository.countAllReviews failed: " + e.getMessage(), e);
        }
    }

    public List<AdminReviewMovieOptionDto> findMoviesHaveReviews() {
        final String sql =
                "SELECT " +
                " m.id AS movie_id, " +
                " m.title AS movie_title, " +
                " COUNT(r.id) AS review_count, " +
                " COALESCE(AVG(r.rating), 0) AS avg_rating " +
                "FROM movie_review r " +
                "JOIN movie m ON m.id = r.movie_id " +
                "GROUP BY m.id, m.title " +
                "ORDER BY m.title ASC";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<AdminReviewMovieOptionDto> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new AdminReviewMovieOptionDto(
                        rs.getInt("movie_id"),
                        rs.getString("movie_title"),
                        rs.getLong("review_count"),
                        rs.getDouble("avg_rating")
                ));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("MovieReviewAdminRepository.findMoviesHaveReviews failed: " + e.getMessage(), e);
        }
    }

    public Boolean toggleHidden(int reviewId) {
        try (Connection cn = dataSource.getConnection()) {
            boolean oldAutoCommit = cn.getAutoCommit();
            cn.setAutoCommit(false);

            try {
                Boolean current;
                String selectSql = "SELECT is_hidden FROM movie_review WHERE id = ?";
                try (PreparedStatement ps = cn.prepareStatement(selectSql)) {
                    ps.setInt(1, reviewId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return null;
                        }
                        current = rs.getBoolean("is_hidden");
                    }
                }

                boolean next = !current;
                String updateSql = "UPDATE movie_review SET is_hidden = ? WHERE id = ?";
                try (PreparedStatement ps = cn.prepareStatement(updateSql)) {
                    ps.setBoolean(1, next);
                    ps.setInt(2, reviewId);
                    int affected = ps.executeUpdate();
                    if (affected != 1) {
                        cn.rollback();
                        return null;
                    }
                }

                cn.commit();
                return next;

            } catch (SQLException e) {
                cn.rollback();
                throw new RuntimeException("toggleHidden failed: " + e.getMessage(), e);
            } finally {
                cn.setAutoCommit(oldAutoCommit);
            }

        } catch (SQLException e) {
            throw new RuntimeException("toggleHidden failed: " + e.getMessage(), e);
        }
    }

    public boolean setHidden(int reviewId, boolean hidden) {
        String sql = "UPDATE movie_review SET is_hidden = ? WHERE id = ?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setBoolean(1, hidden);
            ps.setInt(2, reviewId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("setHidden failed: " + e.getMessage(), e);
        }
    }

    public Boolean getHidden(int reviewId) {
        String sql = "SELECT is_hidden FROM movie_review WHERE id = ?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getBoolean("is_hidden");
            }

        } catch (SQLException e) {
            throw new RuntimeException("getHidden failed: " + e.getMessage(), e);
        }
    }
}