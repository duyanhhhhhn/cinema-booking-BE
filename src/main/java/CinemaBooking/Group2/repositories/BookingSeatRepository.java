package CinemaBooking.Group2.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookingSeatRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public List<Integer> findSoldSeatIdsByShowtime(int showtimeId) {
        String sql = """
            SELECT bs.seat_id 
            FROM booking_seat bs
            JOIN booking b ON bs.booking_id = b.id
            WHERE b.showtime_id = ? 
            AND b.payment_status = 'PAID'
        """;
        try {
            return jdbc.queryForList(sql, Integer.class, showtimeId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch sold seats", e);
        }
    }

    public boolean isSeatSold(int showtimeId, int seatId) {
        String sql = """
            SELECT COUNT(*) 
            FROM booking_seat bs
            JOIN booking b ON bs.booking_id = b.id
            WHERE b.showtime_id = ? 
            AND bs.seat_id = ?
            AND b.payment_status = 'PAID'
        """;
        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, showtimeId, seatId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}