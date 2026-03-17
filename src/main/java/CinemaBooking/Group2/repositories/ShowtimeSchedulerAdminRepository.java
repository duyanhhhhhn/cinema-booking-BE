package CinemaBooking.Group2.repositories;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.scheduler.AdminMovieOptionDto;
import CinemaBooking.Group2.dtos.scheduler.CinemaOptionDto;

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

        Timestamp st = rs.getTimestamp("start_time");
        Timestamp et = rs.getTimestamp("end_time");
        e.startAt = (st == null ? null : st.toLocalDateTime());
        e.endAt = (et == null ? null : et.toLocalDateTime());

        e.basePrice = rs.getBigDecimal("base_price");
        e.status = rs.getString("status");
        return e;
    };

    public List<EventRow> findEventsByCinemaInRange(int cinemaId, LocalDateTime from, LocalDateTime to) {
        String sql = """
            SELECT
                s.id,
                s.room_id,
                s.movie_id,
                s.start_time,
                s.end_time,
                s.base_price,
                s.status,
                m.title,
                m.poster_url
            FROM showtime s
            JOIN room r ON r.id = s.room_id
            JOIN movie m ON m.id = s.movie_id
            WHERE r.cinema_id = ?
              AND (s.status IS NULL OR s.status <> 'CANCELLED')
              AND s.start_time < ?
              AND s.end_time > ?
            ORDER BY s.room_id ASC, s.start_time ASC
        """;
        return jdbc.query(
                sql,
                eventMapper,
                cinemaId,
                Timestamp.valueOf(to),
                Timestamp.valueOf(from)
        );
    }

    public boolean roomBelongsToCinema(int roomId, int cinemaId) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM room WHERE id = ? AND cinema_id = ?",
                Integer.class,
                roomId,
                cinemaId
        );
        return c != null && c > 0;
    }

    public boolean showtimeBelongsToCinema(int showtimeId, int cinemaId) {
        Integer c = jdbc.queryForObject("""
            SELECT COUNT(*)
            FROM showtime s
            JOIN room r ON r.id = s.room_id
            WHERE s.id = ? AND r.cinema_id = ?
        """, Integer.class, showtimeId, cinemaId);
        return c != null && c > 0;
    }

    public String findRoomType(int roomId) {
        List<String> list = jdbc.queryForList(
                "SELECT type FROM room WHERE id = ? LIMIT 1",
                String.class,
                roomId
        );
        if (list.isEmpty()) return null;
        String v = list.get(0);
        return v == null ? null : v.trim();
    }

    public String findMovieFormat(int movieId) {
        List<String> list = jdbc.queryForList(
                "SELECT format FROM movie WHERE id = ? LIMIT 1",
                String.class,
                movieId
        );
        if (list.isEmpty()) return null;
        String v = list.get(0);
        return v == null ? null : v.trim();
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

        Timestamp st = rs.getTimestamp("start_time");
        Timestamp et = rs.getTimestamp("end_time");
        s.startAt = (st == null ? null : st.toLocalDateTime());
        s.endAt = (et == null ? null : et.toLocalDateTime());

        s.basePrice = rs.getBigDecimal("base_price");
        s.status = rs.getString("status");
        return s;
    };

    public ShowtimeInfo findShowtimeById(int id) {
        String sql = """
            SELECT id, movie_id, room_id, start_time, end_time, base_price, status
            FROM showtime
            WHERE id = ?
        """;
        List<ShowtimeInfo> list = jdbc.query(sql, showtimeInfoMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int findMovieDurationMinutes(int movieId) {
        Integer v = jdbc.queryForObject(
                "SELECT duration_minutes FROM movie WHERE id = ?",
                Integer.class,
                movieId
        );
        return v == null ? 0 : v;
    }

    public Integer findAnyOverlapId(int roomId, LocalDateTime startAt, LocalDateTime endAt, Integer excludeId) {
        String sql = """
            SELECT id
            FROM showtime
            WHERE room_id = ?
              AND (status IS NULL OR status <> 'CANCELLED')
              AND start_time < ?
              AND end_time > ?
        """;
        if (excludeId != null) {
            sql += " AND id <> ? ";
        }
        sql += " ORDER BY start_time ASC LIMIT 1";

        if (excludeId != null) {
            List<Integer> ids = jdbc.queryForList(
                    sql,
                    Integer.class,
                    roomId,
                    Timestamp.valueOf(endAt),
                    Timestamp.valueOf(startAt),
                    excludeId
            );
            return ids.isEmpty() ? null : ids.get(0);
        }

        List<Integer> ids = jdbc.queryForList(
                sql,
                Integer.class,
                roomId,
                Timestamp.valueOf(endAt),
                Timestamp.valueOf(startAt)
        );
        return ids.isEmpty() ? null : ids.get(0);
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
        if (excludeId != null) {
            sql += " AND id <> ? ";
        }
        sql += " FOR UPDATE";

        if (excludeId != null) {
            return jdbc.queryForList(
                    sql,
                    Integer.class,
                    roomId,
                    Timestamp.valueOf(endAt),
                    Timestamp.valueOf(startAt),
                    excludeId
            );
        }

        return jdbc.queryForList(
                sql,
                Integer.class,
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
        jdbc.update(
                sql,
                movieId,
                roomId,
                Timestamp.valueOf(startAt),
                Timestamp.valueOf(endAt),
                basePrice
        );
        Integer id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        return id == null ? 0 : id;
    }

    public int updateMove(int id, int roomId, LocalDateTime startAt, LocalDateTime endAt) {
        String sql = """
            UPDATE showtime
            SET room_id = ?, start_time = ?, end_time = ?
            WHERE id = ?
              AND (status IS NULL OR status <> 'CANCELLED')
        """;
        return jdbc.update(
                sql,
                roomId,
                Timestamp.valueOf(startAt),
                Timestamp.valueOf(endAt),
                id
        );
    }

    public int updateStatus(int id, String status) {
        return jdbc.update("UPDATE showtime SET status = ? WHERE id = ?", status, id);
    }

    public List<AdminMovieOptionDto> findMovieOptions(String keyword) {
        return findMovieOptions(keyword, null);
    }

    public List<AdminMovieOptionDto> findMovieOptions(String keyword, String roomType) {
        String baseSql = """
            SELECT id, title, duration_minutes, poster_url, status, format
            FROM movie
            WHERE (status IS NULL OR status <> 'ENDED')
        """;

        java.util.ArrayList<Object> args = new java.util.ArrayList<>();

        if (keyword != null && !keyword.trim().isBlank()) {
            baseSql += " AND title LIKE ? ";
            args.add("%" + keyword.trim() + "%");
        }

        String rt = roomType == null ? "" : roomType.trim().toUpperCase();
        if (!rt.isBlank()) {
            if ("3D".equals(rt)) {
                baseSql += " AND UPPER(COALESCE(format,'')) LIKE '%3D%' ";
            } else if ("IMAX".equals(rt)) {
                baseSql += " AND UPPER(COALESCE(format,'')) LIKE '%IMAX%' ";
            } else if ("2D".equals(rt)) {
                baseSql += " AND UPPER(COALESCE(format,'')) LIKE '%2D%' ";
                baseSql += " AND UPPER(COALESCE(format,'')) NOT LIKE '%3D%' ";
                baseSql += " AND UPPER(COALESCE(format,'')) NOT LIKE '%IMAX%' ";
            } else {
                baseSql += " AND UPPER(COALESCE(format,'')) LIKE ? ";
                args.add("%" + rt + "%");
            }
        }

        baseSql += " ORDER BY created_at DESC, id DESC LIMIT 200";

        return jdbc.query(baseSql, (rs, i) -> {
            AdminMovieOptionDto dto = new AdminMovieOptionDto();
            dto.setId(rs.getInt("id"));
            dto.setTitle(rs.getString("title"));
            dto.setDurationMinutes(rs.getInt("duration_minutes"));
            dto.setPosterUrl(rs.getString("poster_url"));
            dto.setStatus(rs.getString("status"));
            dto.setFormat(rs.getString("format"));
            return dto;
        }, args.toArray());
    }

    public List<AdminMovieOptionDto> findMovieOptionsByRoom(int cinemaId, int roomId, String keyword) {
        String roomSql = """
            SELECT type
            FROM room
            WHERE id = ? AND cinema_id = ?
            LIMIT 1
        """;

        List<String> roomTypes = jdbc.queryForList(roomSql, String.class, roomId, cinemaId);
        if (roomTypes.isEmpty()) {
            return Collections.emptyList();
        }

        String roomType = roomTypes.get(0);
        return findMovieOptions(keyword, roomType);
    }

    public static class ShowtimeDetailRow {
        public int id;
        public int cinemaId;

        public int roomId;
        public String roomName;
        public String roomType;

        public int movieId;
        public String movieTitle;
        public String posterUrl;
        public String movieFormat;
        public int durationMinutes;

        public LocalDateTime startAt;
        public LocalDateTime endAt;
        public BigDecimal basePrice;
        public String status;
    }

    private final RowMapper<ShowtimeDetailRow> showtimeDetailMapper = (ResultSet rs, int i) -> {
        ShowtimeDetailRow d = new ShowtimeDetailRow();
        d.id = rs.getInt("id");
        d.cinemaId = rs.getInt("cinema_id");

        d.roomId = rs.getInt("room_id");
        d.roomName = rs.getString("room_name");
        d.roomType = rs.getString("room_type");

        d.movieId = rs.getInt("movie_id");
        d.movieTitle = rs.getString("movie_title");
        d.posterUrl = rs.getString("poster_url");
        d.movieFormat = rs.getString("movie_format");
        d.durationMinutes = rs.getInt("duration_minutes");

        Timestamp st = rs.getTimestamp("start_time");
        Timestamp et = rs.getTimestamp("end_time");
        d.startAt = (st == null ? null : st.toLocalDateTime());
        d.endAt = (et == null ? null : et.toLocalDateTime());

        d.basePrice = rs.getBigDecimal("base_price");
        d.status = rs.getString("status");
        return d;
    };

    public ShowtimeDetailRow findShowtimeDetail(int showtimeId) {
        String sql = """
            SELECT
                s.id,
                r.cinema_id AS cinema_id,
                s.room_id,
                r.name AS room_name,
                r.type AS room_type,
                s.movie_id,
                m.title AS movie_title,
                m.poster_url,
                m.format AS movie_format,
                m.duration_minutes,
                s.start_time,
                s.end_time,
                s.base_price,
                s.status
            FROM showtime s
            JOIN room r ON r.id = s.room_id
            JOIN movie m ON m.id = s.movie_id
            WHERE s.id = ?
            LIMIT 1
        """;
        List<ShowtimeDetailRow> list = jdbc.query(sql, showtimeDetailMapper, showtimeId);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<CinemaOptionDto> findAllCinemaOptions() {
        String sql = """
            SELECT id, name
            FROM cinema
            WHERE is_active = 1
            ORDER BY name ASC
        """;

        return jdbc.query(sql, (rs, i) -> {
            CinemaOptionDto dto = new CinemaOptionDto();
            dto.setId(rs.getInt("id"));
            dto.setName(rs.getString("name"));
            return dto;
        });
    }

    public int updateEdit(int id, int roomId, int movieId, LocalDateTime startAt, LocalDateTime endAt, BigDecimal basePrice) {
        String sql = """
            UPDATE showtime
            SET room_id = ?, movie_id = ?, start_time = ?, end_time = ?, base_price = ?
            WHERE id = ?
              AND (status IS NULL OR status <> 'CANCELLED')
        """;
        return jdbc.update(
                sql,
                roomId,
                movieId,
                Timestamp.valueOf(startAt),
                Timestamp.valueOf(endAt),
                basePrice,
                id
        );
    }
}