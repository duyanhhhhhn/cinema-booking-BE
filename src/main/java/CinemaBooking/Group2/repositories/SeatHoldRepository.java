package CinemaBooking.Group2.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.SeatHold;

@Repository
public class SeatHoldRepository {

    @Autowired
    private JdbcTemplate jdbc;

    /**
     * Find active (non-expired) seat holds for a showtime
     */
    public List<Integer> findActiveSeatIdsByShowtime(int showtimeId) {
        String sql = """
            SELECT seat_id 
            FROM seat_hold 
            WHERE showtime_id = ? 
            AND hold_expires_at > NOW()
        """;
        try {
            return jdbc.queryForList(sql, Integer.class, showtimeId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch active seat holds", e);
        }
    }

    /**
     * Check if a specific seat is currently held (and not expired)
     */
    public boolean isSeatHeld(int showtimeId, int seatId) {
        String sql = """
            SELECT COUNT(*) 
            FROM seat_hold 
            WHERE showtime_id = ? 
            AND seat_id = ? 
            AND hold_expires_at > NOW()
        """;
        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, showtimeId, seatId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if a specific seat is held by a specific user
     */
    public boolean isSeatHeldByUser(int showtimeId, int seatId, int userId) {
        String sql = """
            SELECT COUNT(*) 
            FROM seat_hold 
            WHERE showtime_id = ? 
            AND seat_id = ? 
            AND user_id = ?
            AND hold_expires_at > NOW()
        """;
        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, showtimeId, seatId, userId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Create a seat hold with race condition handling using INSERT IGNORE
     */
    public boolean holdSeat(int showtimeId, int seatId, int userId, String holdToken, LocalDateTime expiresAt) {
        String sql = """
            INSERT INTO seat_hold (showtime_id, seat_id, user_id, hold_token, hold_expires_at, created_at)
            SELECT ?, ?, ?, ?, ?, NOW()
            WHERE NOT EXISTS (
                SELECT 1 FROM seat_hold 
                WHERE showtime_id = ? 
                AND seat_id = ? 
                AND hold_expires_at > NOW()
            )
        """;
        try {
            int rowsAffected = jdbc.update(sql, showtimeId, seatId, userId, holdToken, expiresAt, 
                                          showtimeId, seatId);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Release a seat hold by user
     */
    public boolean releaseSeat(int showtimeId, int seatId, int userId) {
        String sql = """
            DELETE FROM seat_hold 
            WHERE showtime_id = ? 
            AND seat_id = ? 
            AND user_id = ?
            AND hold_expires_at > NOW()
        """;
        try {
            int rowsAffected = jdbc.update(sql, showtimeId, seatId, userId);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Release seat by hold token
     */
    public boolean releaseSeatByToken(String holdToken) {
        String sql = """
            DELETE FROM seat_hold 
            WHERE hold_token = ?
            AND hold_expires_at > NOW()
        """;
        try {
            int rowsAffected = jdbc.update(sql, holdToken);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clean up expired holds (can be called by a scheduled job)
     */
    public int cleanupExpiredHolds() {
        String sql = "DELETE FROM seat_hold WHERE hold_expires_at <= NOW()";
        try {
            return jdbc.update(sql);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get seat hold details by token
     */
    public SeatHold findByToken(String holdToken) {
        String sql = """
            SELECT * FROM seat_hold 
            WHERE hold_token = ?
            AND hold_expires_at > NOW()
        """;
        try {
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(SeatHold.class), holdToken);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Get all active holds by user for a showtime
     */
    public List<SeatHold> findActiveHoldsByUser(int showtimeId, int userId) {
        String sql = """
            SELECT * FROM seat_hold 
            WHERE showtime_id = ? 
            AND user_id = ?
            AND hold_expires_at > NOW()
        """;
        try {
            return jdbc.query(sql, new BeanPropertyRowMapper<>(SeatHold.class), showtimeId, userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user's seat holds", e);
        }
    }

    /**
     * Delete seat hold regardless of expiration (e.g. forced cleanup)
     */
    public void deleteSeatHold(int showtimeId, int seatId) {
        String sql = "DELETE FROM seat_hold WHERE showtime_id = ? AND seat_id = ?";
        try {
            jdbc.update(sql, showtimeId, seatId);
        } catch (Exception e) {
            // Log or ignore
        }
    }

    /**
     * Get hold expiration for a specific seat in a showtime
     */
    public LocalDateTime getHoldExpiration(int showtimeId, int seatId) {
        String sql = """
            SELECT hold_expires_at 
            FROM seat_hold 
            WHERE showtime_id = ? 
            AND seat_id = ? 
            ORDER BY hold_expires_at DESC 
            LIMIT 1
        """;
        try {
            return jdbc.queryForObject(sql, LocalDateTime.class, showtimeId, seatId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}