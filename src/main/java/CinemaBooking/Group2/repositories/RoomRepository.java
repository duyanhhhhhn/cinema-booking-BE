package CinemaBooking.Group2.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.RoomMapper;
import CinemaBooking.Group2.models.Room;

@Repository
public class RoomRepository {

	@Autowired
	private JdbcTemplate jdbc;

	public List<Room> findByCinema(int cinemaId) {
		String sql = "SELECT * FROM room WHERE cinema_id = ?";
		try {
			return jdbc.query(sql, new RoomMapper(), cinemaId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Room findById(int id) {
		String sql = "SELECT * FROM room WHERE id = ?";
		try {
			return jdbc.query(sql, new RoomMapper(), id).stream().findFirst().orElse(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int insert(Room r) {
		String sql = """
				    INSERT INTO room(cinema_id, name, type, total_seats, seat_layout, created_at)
				    VALUES (?, ?, ?, ?, ?, NOW())
				""";
		try {
			return jdbc.update(sql, r.getCinemaId(), r.getName(), r.getType(), r.getTotalSeats(), r.getSeatLayout());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int update(int id, Room r) {
		String sql = """
				    UPDATE room
				    SET name=?, type=?, total_seats=?, seat_layout=?
				    WHERE id=?
				""";
		try {
			return jdbc.update(sql, r.getName(), r.getType(), r.getTotalSeats(), r.getSeatLayout(), id);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int delete(int id) {
		String sql = "DELETE FROM room WHERE id = ?";
		try {
			return jdbc.update(sql, id);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
