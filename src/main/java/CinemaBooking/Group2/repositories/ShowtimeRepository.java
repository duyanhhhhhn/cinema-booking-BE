package CinemaBooking.Group2.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.movie.MovieWithShowtimesDtos;
import CinemaBooking.Group2.dtos.showtime.MovieShowtimeGroupDtos;
import CinemaBooking.Group2.dtos.showtime.ShowtimePublicDtos;
import CinemaBooking.Group2.models.Showtime;

@Repository
public class ShowtimeRepository {		

	@Autowired
	private DataSource dataSource;
	
    @Autowired
    private JdbcTemplate jdbc;

    public Showtime findById(int showtimeId) {
        String sql = "SELECT * FROM showtime WHERE id = ?";
        try {
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Showtime.class), showtimeId);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean exists(int showtimeId) {
        String sql = "SELECT COUNT(*) FROM showtime WHERE id = ?";
        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, showtimeId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    // (codeser) -> HÀM TRUY VẤN LẤY LỊCH CHIẾU CỦA CÁC BỘ PHIM (RẠP + THỜI GIAN...)
    public List<MovieShowtimeGroupDtos> getShowtimesPublicGrouped(int cinemaId, Integer movieId, LocalDate date) {

        if (cinemaId <= 0) throw new IllegalArgumentException("cinemaId invalid.");
        if (date == null) throw new IllegalArgumentException("date is required (yyyy-MM-dd).");
        if (movieId != null && movieId <= 0) throw new IllegalArgumentException("movieId invalid.");

        boolean hasMovie = (movieId != null && movieId > 0);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        String sql =
            "SELECT " +
            "  m.id    AS movie_id, " +
            "  m.title AS movie_title, " +
            "  COALESCE(m.poster_url, m.banner_url) AS cover_url, " +
            "  s.id    AS showtime_id, " +
            "  TIME_FORMAT(s.start_time, '%H:%i') AS start_time, " +
            "  s.base_price AS price, " +
            "  r.name AS room_name " +
            "FROM showtime s " +
            "JOIN movie  m ON m.id = s.movie_id " +
            "JOIN room   r ON r.id = s.room_id " +
            "JOIN cinema c ON c.id = r.cinema_id " +
            "WHERE c.is_active = 1 " +
            "  AND s.status = 'SCHEDULED' " +
            "  AND m.status IN ('COMING_SOON','NOW_SHOWING') " +
            "  AND r.cinema_id = ? " +
            (hasMovie ? "  AND s.movie_id = ? " : "") +
            "  AND s.start_time >= ? " +
            "  AND s.start_time <  ? " +
            "ORDER BY m.id ASC, s.start_time ASC, r.name ASC, s.id ASC;";

        Map<Integer, MovieShowtimeGroupDtos> map = new LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setReadOnly(true);

            int idx = 1;
            ps.setInt(idx++, cinemaId);

            if (hasMovie) {
                ps.setInt(idx++, movieId);
            }

            ps.setTimestamp(idx++, Timestamp.valueOf(startOfDay));
            ps.setTimestamp(idx++, Timestamp.valueOf(endOfDay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int mid = rs.getInt("movie_id");

                    MovieShowtimeGroupDtos group = map.get(mid);
                    if (group == null) {
                        group = new MovieShowtimeGroupDtos();
                        group.setMovieId(mid);
                        group.setMovieTitle(rs.getString("movie_title"));
                        group.setCoverUrl(rs.getString("cover_url"));
                        group.setShowtimes(new ArrayList<>());
                        map.put(mid, group);
                    }

                    ShowtimePublicDtos item = new ShowtimePublicDtos();
                    item.setId(rs.getInt("showtime_id"));
                    item.setStartTime(rs.getString("start_time"));
                    item.setPrice(rs.getBigDecimal("price"));
                    item.setRoomName(rs.getString("room_name"));

                    group.getShowtimes().add(item);
                }
            }

            return new ArrayList<>(map.values());

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch showtimes (PUBLIC GROUPED) with filters.", e);
        }
    }
    
    public List<Showtime> getShowtimesModel(Integer cinemaId, Integer movieId, LocalDate date) {

        boolean hasCinema = (cinemaId != null && cinemaId > 0);
        boolean hasMovie  = (movieId != null && movieId > 0);
        boolean hasDate   = (date != null);

        LocalDateTime startOfDay = null;
        LocalDateTime endOfDay = null;
        if (hasDate) {
            startOfDay = date.atStartOfDay();
            endOfDay = date.plusDays(1).atStartOfDay();
        }

        String sql =
            "SELECT " +
            "  s.id, s.movie_id, s.room_id, s.start_time, s.end_time, s.base_price, s.status, s.created_at " +
            "FROM showtime s " +
            "JOIN movie  m ON m.id = s.movie_id " +
            "JOIN room   r ON r.id = s.room_id " +
            "JOIN cinema c ON c.id = r.cinema_id " +
            "WHERE 1=1 " +
            "  AND c.is_active = 1 " +
            "  AND s.status = 'SCHEDULED' " +
            "  AND m.status IN ('COMING_SOON', 'NOW_SHOWING') " +
            (hasCinema ? " AND r.cinema_id = ? " : "") +
            (hasMovie  ? " AND s.movie_id  = ? " : "") +
            (hasDate   ? " AND s.start_time >= ? AND s.start_time < ? " : "") +
            "ORDER BY s.start_time ASC, s.id ASC;";

        List<Showtime> items = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;

            if (hasCinema) {
                ps.setInt(idx++, cinemaId);
            }

            if (hasMovie) {
                ps.setInt(idx++, movieId);
            }

            if (hasDate) {
                ps.setTimestamp(idx++, Timestamp.valueOf(startOfDay));
                ps.setTimestamp(idx++, Timestamp.valueOf(endOfDay));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapShowtimeItem(rs));
                }
            }

            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch showtimes (MODEL) with filters.", e);
        }
    }

    
    	
    private Showtime mapShowtimeItem(ResultSet rs) throws SQLException {
        Showtime s = new Showtime();

        s.setId(rs.getInt("id"));
        s.setMovieId(rs.getInt("movie_id"));
        s.setRoomId(rs.getInt("room_id"));

        Timestamp st = rs.getTimestamp("start_time");
        Timestamp et = rs.getTimestamp("end_time");
        Timestamp ca = rs.getTimestamp("created_at");

        s.setStartTime(st != null ? st.toLocalDateTime() : null);
        s.setEndTime(et != null ? et.toLocalDateTime() : null);

        s.setBasePrice(rs.getBigDecimal("base_price"));
        s.setStatus(parseShowtimeStatus(rs.getString("status")));

        s.setCreatedAt(ca != null ? ca.toLocalDateTime() : null);

        return s;
    }

    private Showtime.ShowtimeStatus parseShowtimeStatus(String dbValue) {
        if (dbValue == null) return null;
        try {
            return Showtime.ShowtimeStatus.valueOf(dbValue.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid showtime.status value in DB: " + dbValue, ex);
        }
    }

    public List<MovieWithShowtimesDtos> getCinemasWithShowtimesByMovieId(int movieId) {
        String sql =
            "SELECT " +
            "  c.id AS cinema_id, " +
            "  c.name AS cinema_name, " +
            "  c.address AS address, " +
            "  m.poster_url AS poster_url, " +
            "  m.duration_minutes AS duration_minutes, " +
            "  s.id AS showtime_id, " +
            "  s.start_time AS start_time, " +
            "  r.type AS room_type " +
            "FROM showtime s " +
            "JOIN room r ON r.id = s.room_id " +
            "JOIN cinema c ON c.id = r.cinema_id " +
            "JOIN movie m ON m.id = s.movie_id " +
            "WHERE s.movie_id = ? " +
            "  AND (s.status = 'SCHEDULED' OR s.status IS NULL) " +
            "  AND s.start_time >= NOW() " +
            "  AND (c.is_active = 1 OR c.is_active IS NULL) " +
            "ORDER BY c.id ASC, s.start_time ASC";

        java.time.format.DateTimeFormatter HH_MM = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        java.util.Map<Integer, MovieWithShowtimesDtos> grouped = new java.util.LinkedHashMap<>();

        try (java.sql.Connection con = dataSource.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, movieId);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cinemaId = rs.getInt("cinema_id");

                    MovieWithShowtimesDtos dto = grouped.get(cinemaId);
                    if (dto == null) {
                        dto = new MovieWithShowtimesDtos();
                        dto.setCinemaId(cinemaId);
                        dto.setCinemaName(rs.getString("cinema_name"));
                        dto.setAddress(rs.getString("address"));
                        dto.setPosterUrl(rs.getString("poster_url"));

                        Object durObj = rs.getObject("duration_minutes");
                        dto.setDurationMinutes(durObj == null ? null : ((Number) durObj).intValue());

                        grouped.put(cinemaId, dto);
                    }

                    int showtimeId = rs.getInt("showtime_id");
                    java.time.LocalDateTime startTime =
                        rs.getTimestamp("start_time").toLocalDateTime();

                    String roomType = rs.getString("room_type");
                    String type = (roomType == null || roomType.isBlank()) ? "2D" : roomType.trim();

                    CinemaBooking.Group2.dtos.showtime.ShowtimeItemDtos st =
                        new CinemaBooking.Group2.dtos.showtime.ShowtimeItemDtos();
                    st.setId(showtimeId);
                    st.setStartTime(startTime.format(HH_MM));
                    st.setType(type);

                    dto.getShowtimes().add(st);
                }
            }

            return new java.util.ArrayList<>(grouped.values());

        } catch (java.sql.SQLException e) {
            throw new RuntimeException("FAILED TO FETCH CINEMAS & SHOWTIMES BY MOVIE ID: " + movieId, e);
        }
    }


}
