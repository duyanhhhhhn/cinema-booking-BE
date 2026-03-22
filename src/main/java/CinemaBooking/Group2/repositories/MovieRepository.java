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

    private String effectiveStatusExpr(String alias) {
        String p = (alias == null || alias.isBlank()) ? "" : alias + ".";
        return "CASE " +
               " WHEN " + p + "status = 'ENDED' THEN 'ENDED' " +
               " WHEN " + p + "status = 'HIDDEN' THEN 'HIDDEN' " +
               " WHEN " + p + "end_date IS NOT NULL AND DATE(" + p + "end_date) < CURDATE() THEN 'ENDED' " +
               " WHEN " + p + "release_date IS NOT NULL AND DATE(" + p + "release_date) > CURDATE() THEN 'COMING_SOON' " +
               " WHEN " + p + "release_date IS NOT NULL AND DATE(" + p + "release_date) <= CURDATE() " +
               "      AND (" + p + "end_date IS NULL OR DATE(" + p + "end_date) >= CURDATE()) THEN 'NOW_SHOWING' " +
               " WHEN " + p + "status IS NOT NULL THEN " + p + "status " +
               " ELSE 'COMING_SOON' " +
               "END";
    }

    public List<Movie> getAllMovie(int page, int perPage) {
        if (page < 1) page = 1;
        if (perPage < 1) perPage = 10;

        int offset = (page - 1) * perPage;
        String effectiveStatus = effectiveStatusExpr("m");

        String sql =
            "SELECT " +
            "  m.id, m.title, m.short_description, m.description, m.duration_minutes, " +
            "  m.genre, m.language, m.format, m.director, m.`cast` AS cast, " +
            "  m.poster_url, m.banner_url, m.trailer_url, m.agerating, " +
            "  m.release_date, m.end_date, " +
            "  " + effectiveStatus + " AS status, " +
            "  m.created_at " +
            "FROM movie m " +
            "WHERE (" + effectiveStatus + ") IN ('COMING_SOON', 'NOW_SHOWING') " +
            "ORDER BY " +
            "  CASE (" + effectiveStatus + ") " +
            "    WHEN 'NOW_SHOWING' THEN 0 " +
            "    WHEN 'COMING_SOON' THEN 1 " +
            "    ELSE 2 " +
            "  END, " +
            "  m.created_at DESC, " +
            "  m.id DESC " +
            "LIMIT ? OFFSET ?";

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

    public long countMoviesAdmin(String keyword, Movie.MovieGenre genre) {
        String q = (keyword == null) ? null : keyword.trim();
        if (q != null && q.isBlank()) q = null;

        boolean hasKeyword = (q != null && !q.isEmpty());

        String sql =
            "SELECT COUNT(*) AS total " +
            "FROM movie " +
            "WHERE 1=1 " +
            (hasKeyword
                ? "  AND ( " +
                  "    title LIKE CONCAT(?, '%') COLLATE utf8mb4_0900_ai_ci " +
                  "  ) "
                : "") +
            (genre != null
                ? "  AND REPLACE(UPPER(genre), '-', '_') = ? "
                : "");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;

            if (hasKeyword) {
                ps.setString(idx++, q);
            }

            if (genre != null) {
                ps.setString(idx++, genre.name());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("total");
                return 0L;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count movies (ADMIN) with filters.", e);
        }
    }

    public List<Movie> getAllMovieAdmin(int page, int perPage, String keyword, Movie.MovieGenre genre) {
        if (page < 1) page = 1;
        if (perPage < 1) perPage = 10;

        int offset = (page - 1) * perPage;

        String q = (keyword == null) ? null : keyword.trim();
        boolean hasKeyword = (q != null && !q.isEmpty());

        String sql =
            "SELECT " +
            "  id, title, short_description, description, duration_minutes, genre, language, format, " +
            "  director, `cast` AS cast, poster_url, banner_url, trailer_url, agerating, " +
            "  release_date, end_date, status, created_at " +
            "FROM movie " +
            "WHERE 1=1 " +
            (hasKeyword
                ? "  AND ( " +
                  "    title LIKE CONCAT(?, '%') COLLATE utf8mb4_0900_ai_ci " +
                  "  ) "
                : "") +
            (genre != null
                ? "  AND REPLACE(UPPER(genre), '-', '_') = ? "
                : "") +
            "ORDER BY created_at DESC, id DESC " +
            "LIMIT ? OFFSET ?;";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;

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
                    movie.setAgeRating(rs.getString("agerating"));
                    movie.setReleaseDate(rs.getTimestamp("release_date"));
                    movie.setEndDate(rs.getTimestamp("end_date"));
                    movie.setStatus(parseMovieStatus(rs.getString("status")));
                    movie.setCreatedAt(rs.getTimestamp("created_at"));
                    movies.add(movie);
                }
            }

            return movies;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movies (ADMIN) with filters + pagination.", e);
        }
    }
    
    public List<Movie> getAllMovieCommingSoon(
            int page,
            int perPage,
            String keyword,
            Movie.MovieGenre genre,
            Movie.MovieStatus status
    ) {
        if (page < 1) page = 1;
        if (perPage < 1) perPage = 10;

        int offset = (page - 1) * perPage;

        String q = (keyword == null) ? null : keyword.trim();
        boolean hasKeyword = (q != null && !q.isEmpty());
        boolean hasStatus = (status != null);

        String effectiveStatus = effectiveStatusExpr("m");

        String sql =
            "SELECT " +
            "  m.id, m.title, m.short_description, m.description, m.duration_minutes, m.genre, m.language, m.format, " +
            "  m.director, m.`cast` AS cast, m.poster_url, m.banner_url, m.trailer_url, m.agerating, " +
            "  m.release_date, m.end_date, " +
            "  " + effectiveStatus + " AS status, " +
            "  m.created_at " +
            "FROM movie m " +
            "WHERE (" + effectiveStatus + ") IN (?, ?) " +

            (hasStatus
                ? " AND (" + effectiveStatus + ") = ? "
                : "") +

            (hasKeyword
                ? " AND ( " +
                  "   m.title LIKE CONCAT(?, '%') COLLATE utf8mb4_0900_ai_ci " +
                  " ) "
                : "") +

            (genre != null
                ? " AND REPLACE(UPPER(m.genre), '-', '_') = ? "
                : "") +

            "ORDER BY " +
            " CASE (" + effectiveStatus + ") " +
            "   WHEN 'NOW_SHOWING' THEN 0 " +
            "   WHEN 'COMING_SOON' THEN 1 " +
            "   WHEN 'ENDED' THEN 2 " +
            "   ELSE 3 " +
            " END, " +
            " m.created_at DESC, " +
            " m.id DESC " +
            "LIMIT ? OFFSET ?;";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;

            ps.setString(idx++, Movie.MovieStatus.COMING_SOON.name());
            ps.setString(idx++, Movie.MovieStatus.NOW_SHOWING.name());

            if (hasStatus) {
                ps.setString(idx++, status.name());
            }

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
                    movie.setAgeRating(rs.getString("agerating"));
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
        String effectiveStatus = effectiveStatusExpr("m");

        String sql =
            "SELECT " +
            "  m.id, m.title, m.duration_minutes, m.genre, m.poster_url, m.release_date, m.agerating, " +
            "  " + effectiveStatus + " AS status " +
            "FROM movie m " +
            "WHERE (" + effectiveStatus + ") IN ('COMING_SOON', 'NOW_SHOWING') " +
            "ORDER BY " +
            "  CASE (" + effectiveStatus + ") " +
            "    WHEN 'NOW_SHOWING' THEN 0 " +
            "    WHEN 'COMING_SOON' THEN 1 " +
            "    ELSE 2 " +
            "  END, " +
            "  m.created_at DESC, " +
            "  m.id DESC";

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
                m.setReleaseDate(rs.getTimestamp("release_date"));
                m.setAgeRating(rs.getString("agerating"));
                m.setStatus(parseMovieStatus(rs.getString("status")));
                movies.add(m);
            }

            return movies;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movies (COMING_SOON, NOW_SHOWING).", e);
        }
    }

    public long countMoviesComingSoonNowShowing(
            String keyword,
            Movie.MovieGenre genre,
            Movie.MovieStatus status
    ) {
        String q = (keyword == null) ? null : keyword.trim();
        boolean hasKeyword = (q != null && !q.isEmpty());
        boolean hasStatus = (status != null);

        String effectiveStatus = effectiveStatusExpr("m");

        String sql =
            "SELECT COUNT(*) AS total " +
            "FROM movie m " +
            "WHERE (" + effectiveStatus + ") IN (?, ?) " +
            (hasStatus ? " AND (" + effectiveStatus + ") = ? " : "") +
            (hasKeyword
                ? " AND m.title LIKE CONCAT(?, '%') COLLATE utf8mb4_0900_ai_ci "
                : "") +
            (genre != null
                ? " AND REPLACE(UPPER(m.genre), '-', '_') = ? "
                : "");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;

            ps.setString(idx++, Movie.MovieStatus.COMING_SOON.name());
            ps.setString(idx++, Movie.MovieStatus.NOW_SHOWING.name());

            if (hasStatus) {
                ps.setString(idx++, status.name());
            }

            if (hasKeyword) {
                ps.setString(idx++, q);
            }

            if (genre != null) {
                ps.setString(idx++, genre.name());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("total");
                return 0L;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count movies (COMING_SOON, NOW_SHOWING) with filters.", e);
        }
    }

    public Movie getMovieDetailById(int id) {
        String effectiveStatus = effectiveStatusExpr("m");

        String sql =
            "SELECT " +
            "  m.id, m.title, m.short_description, m.description, m.duration_minutes, " +
            "  m.genre, m.language, m.format, m.director, m.`cast` AS cast, " +
            "  m.poster_url, m.banner_url, m.trailer_url, m.agerating," +
            "  m.release_date, m.end_date, " +
            "  " + effectiveStatus + " AS status, " +
            "  m.created_at " +
            "FROM movie m " +
            "WHERE m.id = ?";

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
            "director, `cast`, poster_url, banner_url, trailer_url, agerating, release_date, end_date, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                "agerating = ?, " +
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

    public List<RelatedMovieItemDtos> getRelatedMoviesByGenre(String genre, int limit) {
        String effectiveStatus = effectiveStatusExpr("m");

        String sql =
            "SELECT m.id, m.title, m.poster_url, m.duration_minutes, m.genre, " +
            "       " + effectiveStatus + " AS status " +
            "FROM movie m " +
            "WHERE m.genre = ? " +
            "  AND (" + effectiveStatus + ") IN ('NOW_SHOWING','COMING_SOON') " +
            "ORDER BY m.created_at DESC, m.id DESC " +
            "LIMIT ?";

        List<RelatedMovieItemDtos> out = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, genre);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer durationMinutes = rs.getObject("duration_minutes") == null
                            ? null : ((Number) rs.getObject("duration_minutes")).intValue();

                    out.add(new RelatedMovieItemDtos(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("poster_url"),
                            durationMinutes,
                            rs.getString("genre"),
                            rs.getString("status")
                    ));
                }
            }
            return out;

        } catch (SQLException e) {
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
        movie.setAgeRating(rs.getString("agerating"));
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

        if (movie.getAgeRating() != null && !movie.getAgeRating().isBlank()) {
            ps.setString(13, movie.getAgeRating().trim());
        } else {
            ps.setNull(13, Types.VARCHAR);
        }

        if (movie.getReleaseDate() != null) {
            ps.setDate(14, new java.sql.Date(movie.getReleaseDate().getTime()));
        } else {
            ps.setNull(14, Types.DATE);
        }

        if (movie.getEndDate() != null) {
            ps.setDate(15, new java.sql.Date(movie.getEndDate().getTime()));
        } else {
            ps.setNull(15, Types.DATE);
        }

        ps.setString(16,
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

            if (movie.getAgeRating() != null && !movie.getAgeRating().isBlank())
                ps.setString(13, movie.getAgeRating().trim());
            else
                ps.setNull(13, Types.VARCHAR);

            if (movie.getReleaseDate() != null)
                ps.setDate(14, new java.sql.Date(movie.getReleaseDate().getTime()));
            else
                ps.setNull(14, Types.DATE);

            if (movie.getEndDate() != null)
                ps.setDate(15, new java.sql.Date(movie.getEndDate().getTime()));
            else
                ps.setNull(15, Types.DATE);

            if (movie.getStatus() != null)
                ps.setString(16, movie.getStatus().name());
            else
                ps.setNull(16, Types.VARCHAR);

            ps.setInt(17, id);

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
            "poster_url, banner_url, trailer_url, agerating, " +
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
                m.setAgeRating(rs.getString("agerating"));

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

        String effectiveStatus = effectiveStatusExpr("m");

        String sql =
            "SELECT COUNT(*) AS total " +
            "FROM movie m " +
            "WHERE (" + effectiveStatus + ") IN (?, ?) " +

            (hasKeyword
                ? " AND ( " +
                  "   m.title LIKE CONCAT(?, '%') COLLATE utf8mb4_0900_as_ci " +
                  " ) "
                : "") +

            (genre != null
                ? " AND REPLACE(UPPER(m.genre), '-', '_') = ? "
                : "");

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

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to count movies (COMING_SOON, NOW_SHOWING).", e);
        }
    }
}
