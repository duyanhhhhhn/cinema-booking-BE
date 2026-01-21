package CinemaBooking.Group2.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public List<ShowtimePublicDtos> getShowtimesPublic(Integer cinemaId, Integer movieId, LocalDate date) {

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
            "  s.id          AS id, " +
            "  s.start_time  AS start_time, " +
            "  s.end_time    AS end_time, " +
            "  s.base_price  AS base_price, " +
            "  s.status      AS status, " +
            "  m.id          AS movie_id, " +
            "  m.title       AS movie_title, " +
            "  m.status      AS movie_status, " +
            "  m.duration_minutes AS duration_minutes, " +
            "  r.id          AS room_id, " +
            "  r.name        AS room_name, " +
            "  c.id          AS cinema_id, " +
            "  c.name        AS cinema_name, " +
            "  c.address     AS cinema_address " +
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
            "ORDER BY s.start_time ASC, r.name ASC, s.id ASC;";

        List<ShowtimePublicDtos> items = new ArrayList<>();

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
                    ShowtimePublicDtos dto = new ShowtimePublicDtos();

                    Timestamp st = rs.getTimestamp("start_time");
                    Timestamp et = rs.getTimestamp("end_time");

                    dto.setId(rs.getInt("id"));
                    dto.setStartTime(st != null ? st.toLocalDateTime() : null);
                    dto.setEndTime(et != null ? et.toLocalDateTime() : null);
                    dto.setBasePrice(rs.getBigDecimal("base_price"));
                    dto.setStatus(rs.getString("status"));

                    dto.setMovieId(rs.getInt("movie_id"));
                    dto.setMovieTitle(rs.getString("movie_title"));
                    dto.setMovieStatus(rs.getString("movie_status"));
                    dto.setDurationMinutes(rs.getInt("duration_minutes"));

                    dto.setRoomId(rs.getInt("room_id"));
                    dto.setRoomName(rs.getString("room_name"));

                    dto.setCinemaId(rs.getInt("cinema_id"));
                    dto.setCinemaName(rs.getString("cinema_name"));
                    dto.setCinemaAddress(rs.getString("cinema_address"));

                    items.add(dto);
                }
            }

            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch showtimes (PUBLIC) with filters.", e);
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

    // Map đúng theo model Showtime bạn đã đưa
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


}
