package CinemaBooking.Group2.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.Seat;

@Repository
public class SeatRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public List<Seat> findSeatsByShowtime(int showtimeId) {
        String sql = """
            SELECT DISTINCT s.* 
            FROM seat s
            JOIN room r ON s.room_id = r.id
            JOIN showtime st ON r.id = st.room_id
            WHERE st.id = ?
            ORDER BY s.seat_code
        """;
        try {
            return jdbc.query(sql, new BeanPropertyRowMapper<>(Seat.class), showtimeId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch seats for showtime", e);
        }
    }

    public Seat findById(int seatId) {
        String sql = "SELECT * FROM seat WHERE id = ?";
        try {
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Seat.class), seatId);
        } catch (Exception e) {
            return null;
        }
    }
}