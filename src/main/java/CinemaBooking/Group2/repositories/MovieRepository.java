package CinemaBooking.Group2.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.movie.MovieMediaDtos;
import CinemaBooking.Group2.dtos.movie.RelatedMovieItemDtos;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.Movie.MovieGenre;

@Repository
public class MovieRepository {

    private final DataSource dataSource;

    @Autowired
    public MovieRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Truy vấn danh sách phim có phân trang.
     */
    public List<Movie> getAllMovie(int page, int perPage) {
        if (page < 1) page = 1;
        if (perPage < 1) perPage = 10;

        int offset = (page - 1) * perPage;

        String sql = """
            SELECT *
            FROM movie
            ORDER BY id DESC
            LIMIT ? OFFSET ?
        """;

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, perPage);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movies.add(mapFullMovie(rs));
                }
            }

            return movies;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movies from database.", e);
        }
    }


    public List<Movie> getAllMovieCommingSoon(int page, int perPage, String keyword, Movie.MovieGenre genre) {
        if (page < 1) page = 1;
        if (perPage < 1) perPage = 10;

        int offset = (page - 1) * perPage;

        String q = (keyword == null) ? null : keyword.trim();
        boolean hasKeyword = (q != null && !q.isEmpty());

        String sql =
            "SELECT " +
            "  id, title, short_description, description, duration_minutes, genre, language, format, " +
            "  director, `cast` AS cast, poster_url, banner_url, trailer_url, " +
            "  release_date, end_date, status, created_at " +
            "FROM movie " +
            "WHERE status IN (?, ?) " +
            (hasKeyword
                ? "  AND ( " +
                  "    title LIKE CONCAT(?, '%') COLLATE utf8mb4_0900_ai_ci " +
                  "  ) "
                : "") +
            (genre != null
                ? "  AND REPLACE(UPPER(genre), '-', '_') = ? "
                : "") +
            "ORDER BY " +
            "  CASE status " +
            "    WHEN 'NOW_SHOWING' THEN 0 " +
            "    WHEN 'COMING_SOON' THEN 1 " +
            "    WHEN 'ENDED' THEN 2 " +
            "    ELSE 3 " +
            "  END, " +
            "  release_date DESC, " +
            "  id DESC " +
            "LIMIT ? OFFSET ?;";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;

            ps.setString(idx++, Movie.MovieStatus.COMING_SOON.name());
            ps.setString(idx++, Movie.MovieStatus.NOW_SHOWING.name());

            if (hasKeyword) {
                ps.setString(idx++, q);
            }

            if (genre != null) {
                ps.setString(idx++, genre.name());
            }

            ps.setInt(idx++, perPage);
            ps.setInt(idx++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie();
                    movie.setId(rs.getInt("id"));
                    movie.setTitle(rs.getString("title"));
                    movie.setShortDescription(rs.getString("short_description"));
                    movie.setDescription(rs.getString("description"));
                    movie.setDurationMinutes(rs.getInt("duration_minutes"));
                    movie.setGenre(parseMovieGenre(rs.getString("genre")));
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
            throw new RuntimeException("Failed to fetch movies (COMING_SOON, NOW_SHOWING) with filters + pagination.", e);
        }
    }

    public List<Movie> getMoviesComingSoonAndNowShowing() {
        String sql = """
            SELECT id, title, duration_minutes, genre, poster_url, status
            FROM movie
            WHERE status IN ('COMING_SOON', 'NOW_SHOWING')
            ORDER BY
              CASE status
                WHEN 'NOW_SHOWING' THEN 1
                WHEN 'COMING_SOON' THEN 2
                ELSE 3
              END,
              release_date DESC
        """;

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Movie m = new Movie();
                m.setId(rs.getInt("id"));
                m.setTitle(rs.getString("title"));
                m.setDurationMinutes(rs.getInt("duration_minutes"));
                m.setGenre(parseMovieGenre(rs.getString("genre")));
                m.setPosterUrl(rs.getString("poster_url"));
                m.setStatus(parseMovieStatus(rs.getString("status")));
                movies.add(m);
            }

            return movies;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movies (COMING_SOON, NOW_SHOWING).", e);
        }
    }

    // Đếm tổng số lượng phim đang ở trạng thái Sắp chiếu hoặc Đang chiếu (không filter).
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

    public MovieMediaDtos getMediaPathById(int id) {
        String sql = "SELECT poster_url, banner_url FROM movie WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new MovieMediaDtos(rs.getString("poster_url"), rs.getString("banner_url"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load media paths for movie id=" + id, e);
        }
    }

    public boolean updateMovieById(int id, Movie movie) {
        String sql =
            "UPDATE movie SET " +
                "title = ?, short_description = ?, description = ?, duration_minutes = ?, " +
                "genre = ?, language = ?, format = ?, director = ?, `cast` = ?, " +
                "poster_url = COALESCE(?, poster_url), " +
                "banner_url = COALESCE(?, banner_url), " +
                "trailer_url = ?, " +
                "release_date = ?, end_date = ?, status = ? " +
            "WHERE id = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindMovieForUpdate(ps, id, movie);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update movie by id=" + id, e);
        }
    }

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
    
    public List<RelatedMovieItemDtos> getRelatedMoviesByGenre(
            String genre, Integer excludeId, int limit
    ) {
        String sql =
            "SELECT id, title, poster_url, duration_minutes, genre, status " +
            "FROM movie " +
            "WHERE genre = ? " +
            (excludeId != null ? "  AND id <> ? " : "") +
            "  AND (status IS NULL OR status IN ('NOW_SHOWING','COMING_SOON')) " +
            "ORDER BY created_at DESC " +
            "LIMIT ?";

        java.util.List<CinemaBooking.Group2.dtos.movie.RelatedMovieItemDtos> out = new java.util.ArrayList<>();

        try (java.sql.Connection con = dataSource.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, genre);

            if (excludeId != null) {
                ps.setInt(i++, excludeId);
            }

            ps.setInt(i++, limit);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object durObj = rs.getObject("duration_minutes");
                    Integer durationMinutes = (durObj == null) ? null : ((Number) durObj).intValue();

                    CinemaBooking.Group2.dtos.movie.RelatedMovieItemDtos dto =
                        new CinemaBooking.Group2.dtos.movie.RelatedMovieItemDtos(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("poster_url"),
                            durationMinutes,
                            rs.getString("genre"),
                            rs.getString("status")
                        );

                    out.add(dto);
                }
            }

            return out;

        } catch (java.sql.SQLException e) {
            throw new RuntimeException("FAILED TO FETCH RELATED MOVIES BY GENRE: " + genre, e);
        }
    }


    private Movie mapFullMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTitle(rs.getString("title"));
        movie.setShortDescription(rs.getString("short_description"));
        movie.setDescription(rs.getString("description"));
        movie.setDurationMinutes(rs.getInt("duration_minutes"));

        movie.setGenre(parseMovieGenre(rs.getString("genre")));

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

    private Movie.MovieStatus parseMovieStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) return null;
        try {
            return Movie.MovieStatus.valueOf(statusStr.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private MovieGenre parseMovieGenre(String raw) {
        if (raw == null || raw.isBlank()) return null;

        String[] parts = raw.split("[,|/]+");

        for (String p : parts) {
            String token = normalizeGenreToken(p);
            if (token == null) continue;

            token = mapGenreAlias(token);

            try {
                return MovieGenre.valueOf(token);
            } catch (IllegalArgumentException ignore) {
            }
        }
        return null;
    }

    private String normalizeGenreToken(String input) {
        if (input == null) return null;
        String s = input.trim();
        if (s.isEmpty()) return null;

        return s.toUpperCase(Locale.ROOT)
                .replace("-", "_")
                .replace(" ", "_");
    }

    private String mapGenreAlias(String token) {
        if (token == null) return null;

        if (token.equals("SCIFI") || token.equals("SCI_FI") || token.equals("SCI__FI")) return "SCI_FI";
        if (token.equals("SCI-FI")) return "SCI_FI";
        if (token.equals("COMING_OF_AGE")) return "COMING_OF_AGE";

        return token;
    }

    private void bindMovieForInsert(PreparedStatement ps, Movie movie) throws SQLException {
        ps.setString(1, movie.getTitle());
        ps.setString(2, movie.getShortDescription());
        ps.setString(3, movie.getDescription());
        ps.setInt(4, movie.getDurationMinutes());

        if (movie.getGenre() != null) ps.setString(5, movie.getGenre().name());
        else ps.setNull(5, Types.VARCHAR);

        ps.setString(6, movie.getLanguage());
        ps.setString(7, movie.getFormat());
        ps.setString(8, movie.getDirector());
        ps.setString(9, movie.getCast());

        ps.setString(10, movie.getPosterUrl());
        ps.setString(11, movie.getBannerUrl());

        if (movie.getTrailerUrl() != null && !movie.getTrailerUrl().isBlank()) {
            ps.setString(12, movie.getTrailerUrl().trim());
        } else {
            ps.setNull(12, Types.VARCHAR);
        }

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

        ps.setString(15,
            movie.getStatus() == null ? Movie.MovieStatus.COMING_SOON.name() : movie.getStatus().name()
        );
    }

    private void bindMovieForUpdate(PreparedStatement ps, int id, Movie movie) {
        try {
            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getShortDescription());
            ps.setString(3, movie.getDescription());
            ps.setInt(4, movie.getDurationMinutes());

            if (movie.getGenre() != null) ps.setString(5, movie.getGenre().name());
            else ps.setNull(5, Types.VARCHAR);

            ps.setString(6, movie.getLanguage());
            ps.setString(7, movie.getFormat());
            ps.setString(8, movie.getDirector());
            ps.setString(9, movie.getCast());

            if (movie.getPosterUrl() != null && !movie.getPosterUrl().isBlank())
                ps.setString(10, movie.getPosterUrl().trim());
            else
                ps.setNull(10, Types.VARCHAR);

            if (movie.getBannerUrl() != null && !movie.getBannerUrl().isBlank())
                ps.setString(11, movie.getBannerUrl().trim());
            else
                ps.setNull(11, Types.VARCHAR);

            if (movie.getTrailerUrl() != null && !movie.getTrailerUrl().isBlank())
                ps.setString(12, movie.getTrailerUrl().trim());
            else
                ps.setNull(12, Types.VARCHAR);

            if (movie.getReleaseDate() != null)
                ps.setDate(13, new java.sql.Date(movie.getReleaseDate().getTime()));
            else
                ps.setNull(13, Types.DATE);

            if (movie.getEndDate() != null)
                ps.setDate(14, new java.sql.Date(movie.getEndDate().getTime()));
            else
                ps.setNull(14, Types.DATE);

            if (movie.getStatus() != null)
                ps.setString(15, movie.getStatus().name());
            else
                ps.setNull(15, Types.VARCHAR);

            ps.setInt(16, id);

        } catch (SQLException e) {
            throw new RuntimeException("BIND MOVIE FOR UPDATE FAILED (ID=" + id + ")", e);
        }
    }

    public long countMovies() {
        String sql = "SELECT COUNT(*) AS total FROM movie";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong("total");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count movies.", e);
        }
    }

    public Movie findById(int id) {
        String sql =
            "SELECT id, title, short_description, description, duration_minutes, " +
            "genre, language, format, director, `cast`, " +
            "poster_url, banner_url, trailer_url, " +
            "release_date, end_date, status, created_at " +
            "FROM movie WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Movie m = new Movie();
                m.setId(rs.getInt("id"));
                m.setTitle(rs.getString("title"));
                m.setShortDescription(rs.getString("short_description"));
                m.setDescription(rs.getString("description"));

                Integer dur = (Integer) rs.getObject("duration_minutes");
                m.setDurationMinutes(dur != null ? dur : 0);

                m.setGenre(parseMovieGenre(rs.getString("genre")));

                m.setLanguage(rs.getString("language"));
                m.setFormat(rs.getString("format"));
                m.setDirector(rs.getString("director"));
                m.setCast(rs.getString("cast"));

                m.setPosterUrl(rs.getString("poster_url"));
                m.setBannerUrl(rs.getString("banner_url"));
                m.setTrailerUrl(rs.getString("trailer_url"));

                java.sql.Timestamp releaseTs = rs.getTimestamp("release_date");
                m.setReleaseDate(releaseTs != null ? new java.util.Date(releaseTs.getTime()) : null);

                java.sql.Timestamp endTs = rs.getTimestamp("end_date");
                m.setEndDate(endTs != null ? new java.util.Date(endTs.getTime()) : null);

                String statusStr = rs.getString("status");
                if (statusStr != null && !statusStr.isBlank()) {
                    try {
                        m.setStatus(Movie.MovieStatus.valueOf(statusStr.trim()));
                    } catch (IllegalArgumentException ex) {
                        m.setStatus(null);
                    }
                } else {
                    m.setStatus(null);
                }

                java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
                m.setCreatedAt(createdTs != null ? new java.util.Date(createdTs.getTime()) : null);

                return m;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find movie by id=" + id, e);
        }
    }

    public int countMoviesComingSoonNowShowing(String keyword, Movie.MovieGenre genre) {
        String q = (keyword == null) ? null : keyword.trim();
        boolean hasKeyword = (q != null && !q.isEmpty());

        String sql =
            "SELECT COUNT(*) AS total " +
            "FROM movie " +
            "WHERE status IN (?, ?) " +
            (hasKeyword
                ? " AND ( " +
                  "   LOWER(title) LIKE CONCAT('%', LOWER(?), '%') " +
                  "   OR LOWER(short_description) LIKE CONCAT('%', LOWER(?), '%') " +
                  "   OR LOWER(description) LIKE CONCAT('%', LOWER(?), '%') " +
                  "   OR LOWER(director) LIKE CONCAT('%', LOWER(?), '%') " +
                  "   OR LOWER(`cast`) LIKE CONCAT('%', LOWER(?), '%') " +
                  " ) "
                : "") +
            (genre != null
                ? " AND REPLACE(UPPER(genre), '-', '_') = ? "
                : "");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            ps.setString(idx++, Movie.MovieStatus.COMING_SOON.name());
            ps.setString(idx++, Movie.MovieStatus.NOW_SHOWING.name());

            if (hasKeyword) {
                ps.setString(idx++, q);
                ps.setString(idx++, q);
                ps.setString(idx++, q);
                ps.setString(idx++, q);
                ps.setString(idx++, q);
            }

            if (genre != null) {
                ps.setString(idx++, genre.name());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count movies (COMING_SOON, NOW_SHOWING) with filters.", e);
        }
    }
}
