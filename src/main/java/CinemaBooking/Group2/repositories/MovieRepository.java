package CinemaBooking.Group2.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.Movie;

@Repository
public class MovieRepository {

    private final DataSource dataSource;

    @Autowired
    public MovieRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Truy vấn toàn bộ danh sách phim có trong hệ thống.
     */
    public List<Movie> getAllMovie() {
        String sql = "SELECT * FROM movie";
        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                movies.add(mapFullMovie(rs));
            }

            return movies;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movies from database.", e);
        }
    }


    /**
     * Lấy danh sách phim sắp chiếu và đang chiếu có áp dụng phân trang.
     */
    public List<Movie> getAllMovieCommingSoon(int page, int perPage) {
        if (page < 1) page = 1;
        if (perPage < 1) perPage = 10;

        int offset = (page - 1) * perPage;

        String sql =
            "SELECT " +
            "  id, title, short_description, description, duration_minutes, genre, language, format, " +
            "  director, `cast` AS cast, poster_url, banner_url, trailer_url, " +
            "  release_date, end_date, status, created_at " +
            "FROM movie " +
            "WHERE status IN (?, ?) " +
            "ORDER BY id DESC " +
            "LIMIT ? OFFSET ?;";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, Movie.MovieStatus.COMING_SOON.name());
            ps.setString(2, Movie.MovieStatus.NOW_SHOWING.name());
            ps.setInt(3, perPage);
            ps.setInt(4, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie();
                    movie.setId(rs.getInt("id"));
                    movie.setTitle(rs.getString("title"));
                    movie.setShortDescription(rs.getString("short_description"));
                    movie.setDescription(rs.getString("description"));
                    movie.setDurationMinutes(rs.getInt("duration_minutes"));
                    movie.setGenre(rs.getString("genre"));
                    movie.setLanguage(rs.getString("language"));
                    movie.setFormat(rs.getString("format"));
                    movie.setDirector(rs.getString("director"));
                    movie.setCast(rs.getString("cast"));
                    movie.setPosterUrl(rs.getString("poster_url"));
                    movie.setBannerUrl(rs.getString("banner_url"));
                    movie.setTrailerUrl(rs.getString("trailer_url"));
                    movie.setReleaseDate(rs.getTimestamp("release_date"));
                    movie.setEndDate(rs.getTimestamp("end_date"));
                    movie.setStatus(parseMovieStatus(rs.getString("status")));
                    movie.setCreatedAt(rs.getTimestamp("created_at"));
                    movies.add(movie);
                }
            }
            return movies;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movies (COMING_SOON, NOW_SHOWING) with pagination.", e);
        }
    }


    /**
     * Đếm tổng số lượng phim đang ở trạng thái Sắp chiếu hoặc Đang chiếu.
     */
    public int countMovieComingSoonNowShowing() {
        String sql = "SELECT COUNT(*) FROM movie WHERE status IN (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, Movie.MovieStatus.COMING_SOON.name());
            ps.setString(2, Movie.MovieStatus.NOW_SHOWING.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count movies by status.", e);
        }
    }


    /**
     * Truy vấn thông tin chi tiết của một bộ phim cụ thể qua ID.
     */
    public Movie getMovieDetailById(int id) {
        String sql = "SELECT * FROM movie WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapFullMovie(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movie detail by id=" + id, e);
        }
    }


    /**
     * Thêm mới phim vào database và trả về ID tự động phát sinh.
     */
    public int createNewMovieReturnId(Movie movie) {
        String sql =
            "INSERT INTO movie " +
            "(title, short_description, description, duration_minutes, genre, language, format, " +
            "director, `cast`, poster_url, banner_url, trailer_url, release_date, end_date, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindMovieForInsert(ps, movie);

            int affected = ps.executeUpdate();
            if (affected <= 0) return 0;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert new movie and return id.", e);
        }
    }


    /**
     * Cập nhật đường dẫn ảnh Poster và Banner cho phim dựa trên ID.
     */
    public void updateMovieImages(int movieId, String posterPath, String bannerPath) {
        if ((posterPath == null || posterPath.isBlank()) && (bannerPath == null || bannerPath.isBlank())) {
            return;
        }

        StringBuilder sql = new StringBuilder("UPDATE movie SET ");
        List<Object> params = new ArrayList<>();

        if (posterPath != null && !posterPath.isBlank()) {
            sql.append("poster_url = ?");
            params.add(posterPath);
        }

        if (bannerPath != null && !bannerPath.isBlank()) {
            if (!params.isEmpty()) sql.append(", ");
            sql.append("banner_url = ?");
            params.add(bannerPath);
        }

        sql.append(" WHERE id = ?;");
        params.add(movieId);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update movie images for id=" + movieId, e);
        }
    }


    /**
     * Xóa hoàn toàn một bộ phim khỏi database dựa trên ID.
     */
    public boolean deleteMovieById(int id) {
        String sql = "DELETE FROM movie WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete movie by id.", e);
        }
    }


    /**
     * Cập nhật toàn bộ thông tin chỉnh sửa của phim theo ID.
     */
    public boolean updateMovieById(int id, Movie movie) {
        String sql =
            "UPDATE movie SET " +
            "title = ?, short_description = ?, description = ?, duration_minutes = ?, " +
            "genre = ?, language = ?, format = ?, director = ?, `cast` = ?, " +
            "poster_url = ?, banner_url = ?, trailer_url = ?, " +
            "release_date = ?, end_date = ?, status = ? WHERE id = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindMovieForUpdate(ps, id, movie);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update movie by id=" + id, e);
        }
    }


    /**
     * Kiểm tra sự tồn tại của phim trong hệ thống.
     */
    public boolean existsById(int id) {
        String sql = "SELECT 1 FROM movie WHERE id = ? LIMIT 1;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check movie existence by id=" + id, e);
        }
    }


    /**
     * Chuyển đổi dữ liệu từ ResultSet sang đối tượng Movie (Mapping).
     */
    private Movie mapFullMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTitle(rs.getString("title"));
        movie.setShortDescription(rs.getString("short_description"));
        movie.setDescription(rs.getString("description"));
        movie.setDurationMinutes(rs.getInt("duration_minutes"));
        movie.setGenre(rs.getString("genre"));
        movie.setLanguage(rs.getString("language"));
        movie.setFormat(rs.getString("format"));
        movie.setDirector(rs.getString("director"));
        movie.setCast(rs.getString("cast"));
        movie.setPosterUrl(rs.getString("poster_url"));
        movie.setBannerUrl(rs.getString("banner_url"));
        movie.setTrailerUrl(rs.getString("trailer_url"));
        movie.setReleaseDate(rs.getDate("release_date"));
        movie.setEndDate(rs.getDate("end_date"));
        movie.setCreatedAt(rs.getTimestamp("created_at"));
        movie.setStatus(parseMovieStatus(rs.getString("status")));
        return movie;
    }


    /**
     * Chuyển đổi chuỗi String từ DB sang Enum MovieStatus một cách an toàn.
     */
    private Movie.MovieStatus parseMovieStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) return null;
        try {
            return Movie.MovieStatus.valueOf(statusStr.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    /**
     * Gán tham số dữ liệu cho câu lệnh PreparedStatement khi Insert phim.
     */
    private void bindMovieForInsert(PreparedStatement ps, Movie movie) throws SQLException {
        ps.setString(1, movie.getTitle());
        ps.setString(2, movie.getShortDescription());
        ps.setString(3, movie.getDescription());
        ps.setInt(4, movie.getDurationMinutes());
        ps.setString(5, movie.getGenre());
        ps.setString(6, movie.getLanguage());
        ps.setString(7, movie.getFormat());
        ps.setString(8, movie.getDirector());
        ps.setString(9, movie.getCast());
        ps.setString(10, movie.getPosterUrl());
        ps.setString(11, movie.getBannerUrl());
        ps.setString(12, movie.getTrailerUrl());

        if (movie.getReleaseDate() != null) {
            ps.setDate(13, new java.sql.Date(movie.getReleaseDate().getTime()));
        } else {
            ps.setNull(13, Types.DATE);
        }

        if (movie.getEndDate() != null) {
            ps.setDate(14, new java.sql.Date(movie.getEndDate().getTime()));
        } else {
            ps.setNull(14, Types.DATE);
        }

        ps.setString(15, movie.getStatus() == null ? Movie.MovieStatus.COMING_SOON.name() : movie.getStatus().name());
    }


    /**
     * Gán tham số dữ liệu cho câu lệnh PreparedStatement khi Update phim.
     */
    private void bindMovieForUpdate(PreparedStatement ps, int id, Movie movie) throws SQLException {
        ps.setString(1, movie.getTitle());
        ps.setString(2, movie.getShortDescription());
        ps.setString(3, movie.getDescription());
        ps.setInt(4, movie.getDurationMinutes());
        ps.setString(5, movie.getGenre());
        ps.setString(6, movie.getLanguage());
        ps.setString(7, movie.getFormat());
        ps.setString(8, movie.getDirector());
        ps.setString(9, movie.getCast());
        ps.setString(10, movie.getPosterUrl());
        ps.setString(11, movie.getBannerUrl());
        ps.setString(12, movie.getTrailerUrl());

        if (movie.getReleaseDate() != null) {
            ps.setDate(13, new java.sql.Date(movie.getReleaseDate().getTime()));
        } else {
            ps.setNull(13, Types.DATE);
        }

        if (movie.getEndDate() != null) {
            ps.setDate(14, new java.sql.Date(movie.getEndDate().getTime()));
        } else {
            ps.setNull(14, Types.DATE);
        }

        if (movie.getStatus() != null) {
            ps.setString(15, movie.getStatus().name());
        } else {
            ps.setNull(15, Types.VARCHAR);
        }

        ps.setInt(16, id);
    }
}