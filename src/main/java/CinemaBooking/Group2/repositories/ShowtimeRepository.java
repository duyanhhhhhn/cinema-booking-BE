package CinemaBooking.Group2.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.Showtime;

@Repository
public class ShowtimeRepository {

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
}
