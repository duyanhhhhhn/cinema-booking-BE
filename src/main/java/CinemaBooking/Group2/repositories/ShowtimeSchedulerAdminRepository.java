package CinemaBooking.Group2.repositories;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.scheduler.AdminMovieOptionDto;

@Repository
public class ShowtimeSchedulerAdminRepository {

    private final JdbcTemplate jdbc;

    public ShowtimeSchedulerAdminRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public static class RoomRow {
        public int id;
        public String name;
        public String type;
        public int totalSeats;
    }

    private final RowMapper<RoomRow> roomMapper = (ResultSet rs, int i) -> {
        RoomRow r = new RoomRow();
        r.id = rs.getInt("id");
        r.name = rs.getString("name");
        r.type = rs.getString("type");
        r.totalSeats = rs.getInt("total_seats");
        return r;
    };

    public List<RoomRow> findRoomsByCinema(int cinemaId) {
        String sql = """
            SELECT id, name, type, total_seats
            FROM room
            WHERE cinema_id = ?
            ORDER BY id ASC
        """;
        return jdbc.query(sql, roomMapper, cinemaId);
    }

    public static class EventRow {
        public int id;
        public int roomId;
        public int movieId;
        public String movieTitle;
        public String posterUrl;
        public LocalDateTime startAt;
        public LocalDateTime endAt;
        public BigDecimal basePrice;
        public String status;
    }

    private final RowMapper<EventRow> eventMapper = (ResultSet rs, int i) -> {
        EventRow e = new EventRow();
        e.id = rs.getInt("id");
        e.roomId = rs.getInt("room_id");
        e.movieId = rs.getInt("movie_id");
        e.movieTitle = rs.getString("title");
        e.posterUrl = rs.getString("poster_url");
        e.startAt = rs.getTimestamp("start_time").toLocalDateTime();
        e.endAt = rs.getTimestamp("end_time").toLocalDateTime();
        e.basePrice = rs.getBigDecimal("base_price");
        e.status = rs.getString("status");
        return e;
    };

    public List<EventRow> findEventsByCinemaInRange(int cinemaId, LocalDateTime from, LocalDateTime to) {
        String sql = """
            SELECT
                s.id, s.room_id, s.movie_id, s.start_time, s.end_time, s.base_price, s.status,
                m.title, m.poster_url
            FROM showtime s
            JOIN room r ON r.id = s.room_id
            JOIN movie m ON m.id = s.movie_id
            WHERE r.cinema_id = ?
              AND (s.status IS NULL OR s.status <> 'CANCELLED')
              AND s.start_time < ?
              AND s.end_time > ?
            ORDER BY s.room_id ASC, s.start_time ASC
        """;
        return jdbc.query(sql, eventMapper,
                cinemaId,
                Timestamp.valueOf(to),
                Timestamp.valueOf(from)
        );
    }

    public boolean roomBelongsToCinema(int roomId, int cinemaId) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM room WHERE id = ? AND cinema_id = ?",
                Integer.class, roomId, cinemaId
        );
        return c != null && c > 0;
    }

    public static class ShowtimeInfo {
        public int id;
        public int movieId;
        public int roomId;
        public LocalDateTime startAt;
        public LocalDateTime endAt;
        public BigDecimal basePrice;
        public String status;
    }

    private final RowMapper<ShowtimeInfo> showtimeInfoMapper = (ResultSet rs, int i) -> {
        ShowtimeInfo s = new ShowtimeInfo();
        s.id = rs.getInt("id");
        s.movieId = rs.getInt("movie_id");
        s.roomId = rs.getInt("room_id");
        s.startAt = rs.getTimestamp("start_time").toLocalDateTime();
        s.endAt = rs.getTimestamp("end_time").toLocalDateTime();
        s.basePrice = rs.getBigDecimal("base_price");
        s.status = rs.getString("status");
        return s;
    };

    public ShowtimeInfo findShowtimeById(int id) {
        String sql = "SELECT id, movie_id, room_id, start_time, end_time, base_price, status FROM showtime WHERE id = ?";
        List<ShowtimeInfo> list = jdbc.query(sql, showtimeInfoMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int findMovieDurationMinutes(int movieId) {
        Integer v = jdbc.queryForObject("SELECT duration_minutes FROM movie WHERE id = ?", Integer.class, movieId);
        return v == null ? 0 : v;
    }

    public List<Integer> lockOverlaps(int roomId, LocalDateTime startAt, LocalDateTime endAt, Integer excludeId) {
        String sql = """
            SELECT id
            FROM showtime
            WHERE room_id = ?
              AND (status IS NULL OR status <> 'CANCELLED')
              AND start_time < ?
              AND end_time > ?
        """;
        if (excludeId != null) sql += " AND id <> ? ";
        sql += " FOR UPDATE";

        if (excludeId != null) {
            return jdbc.queryForList(sql, Integer.class,
                    roomId,
                    Timestamp.valueOf(endAt),
                    Timestamp.valueOf(startAt),
                    excludeId
            );
        }
        return jdbc.queryForList(sql, Integer.class,
                roomId,
                Timestamp.valueOf(endAt),
                Timestamp.valueOf(startAt)
        );
    }

    public int insertShowtime(int movieId, int roomId, LocalDateTime startAt, LocalDateTime endAt, BigDecimal basePrice) {
        String sql = """
            INSERT INTO showtime (movie_id, room_id, start_time, end_time, base_price, status)
            VALUES (?, ?, ?, ?, ?, 'SCHEDULED')
        """;
        jdbc.update(sql,
                movieId,
                roomId,
                Timestamp.valueOf(startAt),
                Timestamp.valueOf(endAt),
                basePrice
        );
        Integer id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        return id == null ? 0 : id;
    }

    public void updateMove(int id, int roomId, LocalDateTime startAt, LocalDateTime endAt) {
        String sql = "UPDATE showtime SET room_id = ?, start_time = ?, end_time = ? WHERE id = ?";
        jdbc.update(sql,
                roomId,
                Timestamp.valueOf(startAt),
                Timestamp.valueOf(endAt),
                id
        );
    }

    public void updateStatus(int id, String status) {
        jdbc.update("UPDATE showtime SET status = ? WHERE id = ?", status, id);
    }
    
    public List<AdminMovieOptionDto> findMovieOptions(String keyword) {
        String baseSql = """
            SELECT id, title, duration_minutes, poster_url, status
            FROM movie
            WHERE (status IS NULL OR status <> 'ENDED')
        """;

        Object[] args;
        if (keyword != null && !keyword.trim().isBlank()) {
            baseSql += " AND title LIKE ? ";
            args = new Object[] { "%" + keyword.trim() + "%" };
        } else {
            args = new Object[] {};
        }

        baseSql += " ORDER BY created_at DESC, id DESC LIMIT 200";

        return jdbc.query(baseSql, (rs, i) -> {
            AdminMovieOptionDto dto = new AdminMovieOptionDto();
            dto.setId(rs.getInt("id"));
            dto.setTitle(rs.getString("title"));
            dto.setDurationMinutes(rs.getInt("duration_minutes"));
            dto.setPosterUrl(rs.getString("poster_url"));
            dto.setStatus(rs.getString("status"));
            return dto;
        }, args);
    }
}