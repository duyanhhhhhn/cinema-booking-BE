package CinemaBooking.Group2.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.showtime.ShowtimeDetailDtos;
import CinemaBooking.Group2.dtos.showtime.ShowtimeSeatItemDtos;

@Repository
public class ShowtimeSheetMapRepositories {

    private final DataSource dataSource;

    public ShowtimeSheetMapRepositories(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String SQL_HEADER =
        "SELECT " +
        "  s.id          AS showtime_id, " +
        "  s.movie_id    AS movie_id, " +
        "  m.title       AS movie_title, " +
        "  m.status      AS movie_status, " +
        "  m.duration_minutes AS duration_minutes, " +
        "  s.room_id     AS room_id, " +
        "  r.name        AS room_name, " +
        "  c.id          AS cinema_id, " +
        "  c.name        AS cinema_name, " +
        "  c.address     AS cinema_address, " +
        "  s.start_time  AS start_time, " +
        "  s.end_time    AS end_time, " +
        "  s.base_price  AS base_price, " +
        "  s.status      AS showtime_status " +
        "FROM showtime s " +
        "JOIN movie  m ON m.id = s.movie_id " +
        "JOIN room   r ON r.id = s.room_id " +
        "JOIN cinema c ON c.id = r.cinema_id " +
        "WHERE s.id = ? " +
        "  AND c.is_active = 1;";

    private static final String SQL_SEATS =
        "SELECT " +
        "  se.id        AS seat_id, " +
        "  se.seat_code AS seat_code, " +
        "  se.seat_type AS seat_type, " +
        "  se.extra_price AS extra_price, " +
        "  CASE " +
        "    WHEN booked.seat_id IS NOT NULL THEN 'BOOKED' " +
        "    WHEN held.seat_id   IS NOT NULL THEN 'HELD' " +
        "    ELSE 'AVAILABLE' " +
        "  END AS seat_status, " +
        "  held.hold_expires_at AS hold_expires_at " +
        "FROM showtime s " +
        "JOIN seat se ON se.room_id = s.room_id " +
        "LEFT JOIN ( " +
        "  SELECT bs.seat_id " +
        "  FROM booking_seat bs " +
        "  JOIN booking b ON b.id = bs.booking_id " +
        "  LEFT JOIN payment p " +
        "    ON p.booking_id = b.id " +
        "   AND p.status = 'SUCCESS' " +
        "  WHERE b.showtime_id = ? " +
        "    AND (b.payment_status = 'PAID' OR p.id IS NOT NULL) " +
        "  GROUP BY bs.seat_id " +
        ") booked ON booked.seat_id = se.id " +
        "LEFT JOIN ( " +
        "  SELECT sh.seat_id, sh.hold_expires_at " +
        "  FROM seat_hold sh " +
        "  WHERE sh.showtime_id = ? " +
        "    AND sh.hold_expires_at > NOW() " +
        ") held ON held.seat_id = se.id " +
        "WHERE s.id = ? " +
        "ORDER BY se.seat_code ASC;";

    public ShowtimeDetailDtos getShowtimeDetailWithSeatMap(int showtimeId) {
        if (showtimeId <= 0) {
            throw new IllegalArgumentException("showtimeId invalid.");
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setReadOnly(true);

            ShowtimeDetailDtos header = fetchHeader(conn, showtimeId);
            if (header == null) {
                return null;
            }

            List<ShowtimeSeatItemDtos> seats = fetchSeats(conn, showtimeId);
            header.setSeats(seats);

            return header;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch showtime detail + seat map.", e);
        }
    }

    private ShowtimeDetailDtos fetchHeader(Connection conn, int showtimeId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_HEADER)) {
            ps.setInt(1, showtimeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapHeader(rs);
            }
        }
    }

    private List<ShowtimeSeatItemDtos> fetchSeats(Connection conn, int showtimeId) throws SQLException {
        List<ShowtimeSeatItemDtos> seats = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SQL_SEATS)) {
            int idx = 1;
            ps.setInt(idx++, showtimeId); // booked subquery: b.showtime_id = ?
            ps.setInt(idx++, showtimeId); // held  subquery: sh.showtime_id = ?
            ps.setInt(idx++, showtimeId); // WHERE s.id = ?

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapSeat(rs));
                }
            }
        }

        return seats;
    }

    private ShowtimeDetailDtos mapHeader(ResultSet rs) throws SQLException {
        ShowtimeDetailDtos dto = new ShowtimeDetailDtos();

        dto.setShowtimeId(rs.getInt("showtime_id"));
        dto.setMovieId(rs.getInt("movie_id"));
        dto.setMovieTitle(rs.getString("movie_title"));
        dto.setMovieStatus(rs.getString("movie_status"));
        dto.setDurationMinutes(rs.getInt("duration_minutes"));

        dto.setRoomId(rs.getInt("room_id"));
        dto.setRoomName(rs.getString("room_name"));

        dto.setCinemaId(rs.getInt("cinema_id"));
        dto.setCinemaName(rs.getString("cinema_name"));
        dto.setCinemaAddress(rs.getString("cinema_address"));

        Timestamp st = rs.getTimestamp("start_time");
        Timestamp et = rs.getTimestamp("end_time");
        dto.setStartTime(st != null ? st.toLocalDateTime() : null);
        dto.setEndTime(et != null ? et.toLocalDateTime() : null);

        dto.setBasePrice(rs.getBigDecimal("base_price"));
        dto.setShowtimeStatus(rs.getString("showtime_status"));

        return dto;
    }

    private ShowtimeSeatItemDtos mapSeat(ResultSet rs) throws SQLException {
        ShowtimeSeatItemDtos seat = new ShowtimeSeatItemDtos();

        seat.setSeatId(rs.getInt("seat_id"));
        seat.setSeatCode(rs.getString("seat_code"));
        seat.setSeatType(rs.getString("seat_type"));
        seat.setExtraPrice(rs.getBigDecimal("extra_price"));
        seat.setSeatStatus(rs.getString("seat_status"));

        Timestamp hx = rs.getTimestamp("hold_expires_at");
        seat.setHoldExpiresAt(hx != null ? hx.toLocalDateTime() : null);

        return seat;
    }
}
