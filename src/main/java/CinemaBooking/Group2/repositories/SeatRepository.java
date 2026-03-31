package CinemaBooking.Group2.repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;

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

//	public List<Seat> getSeatsByRoomId(int roomId) {
//		String sql = "SELECT * FROM seat WHERE room_id = ?";
//		try {
//			return jdbc.query(sql, new SeatMapper(), roomId);
//		} catch (Exception e) {
//			throw new RuntimeException("Failed to fetch seats for room", e);
//		}
//	}

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

	// ================= INSERT SEAT =================
	public int insertSeat(Seat seat) {
		String sql = """
                    INSERT INTO seat (room_id, seat_code, seat_type, extra_price)
                    VALUES (?, ?, ?, ?)
                """;

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setInt(1, seat.getRoomId());
                ps.setString(2, seat.getSeatCode());
                ps.setString(3, seat.getSeatType().name());
                ps.setObject(4, seat.getExtraPrice());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            int generatedId = key == null ? -1 : key.intValue();
            if (generatedId != -1) seat.setId(generatedId);
            return generatedId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert seat", e);
        }
	}

	public void deleteByRoomId(int roomId) {
		String sql = "DELETE FROM seat WHERE room_id = ?";
		try {
			jdbc.update(sql, roomId);
		} catch (Exception e) {
			throw new RuntimeException("Failed to delete seats for room", e);
		}
	}

	public int updateSeat(Seat seat) {
		String sql = """
				    UPDATE seat
				    SET seat_code = ?, seat_type = ?, extra_price = ?
				    WHERE id = ?
				""";

		try {
			return jdbc.update(sql, seat.getSeatCode(), seat.getSeatType().name(), seat.getExtraPrice(), seat.getId());
		} catch (Exception e) {
			throw new RuntimeException("Failed to update seat", e);
		}
	}

	public void deleteByIds(List<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty()) return; // nothing to delete
            String sql = "DELETE FROM seat WHERE id IN (" +
                    ids.stream().map(i -> "?").reduce((a, b) -> a + "," + b).orElse("") +
                    ")";
            jdbc.update(sql, ids.toArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete seats by IDs", e);
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

}