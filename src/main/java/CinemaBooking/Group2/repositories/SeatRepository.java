package CinemaBooking.Group2.repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.SeatMapper;
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

	public int getRoomIdByShowtime(int showtimeId) {
		String sql = "SELECT room_id FROM showtime WHERE id = ?";
		try {
			return jdbc.queryForObject(sql, Integer.class, showtimeId);
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch room ID for showtime", e);
		}
	}

	public List<Seat> getSeatsByRoomId(int roomId) {
		String sql = "SELECT * FROM seat WHERE room_id = ?";
		try {
			return jdbc.query(sql, new SeatMapper(), roomId);
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch seats for room", e);
		}
	}

	public Map<Integer, String> getBookedSeats(int showtimeId) {

		String sql = """
				    SELECT bs.seat_id, b.payment_status
				    FROM booking_seat bs
				    JOIN booking b
				      ON b.id = bs.booking_id
				    WHERE b.showtime_id = ?
				      AND b.payment_status IN ('PAID','PENDING')
				""";

		try {
			return jdbc.query(sql, rs -> {

				Map<Integer, String> map = new HashMap<>();

				while (rs.next()) {

					map.put(rs.getInt("seat_id"), rs.getString("payment_status"));
				}

				return map;

			}, showtimeId);
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch booked seats for showtime", e);

		}
	}

}