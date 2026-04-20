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

	// ================= FIND ALL ROOMS =================
	public List<Room> findAll() {
		String sql = "SELECT * FROM room";
		try {
			return jdbc.query(sql, new RoomMapper());
		} catch (Exception e) {
			e.printStackTrace();
			return List.of(); // Return empty list on error
		}
	}

	// ================= FIND BY CINEMA =================
	public List<Room> findByCinema(int cinemaId) {

		String sql = "SELECT * FROM room WHERE cinema_id = ?";

		try {
			return jdbc.query(sql, new RoomMapper(), cinemaId);
		} catch (Exception e) {
			e.printStackTrace();
			return List.of(); // Return empty list on error
		}
	}
	
	// ================= FIND BY CINEMA + STATUS =================
	public List<Room> findWithFilter(Integer cinemaId, Integer status) {

	    StringBuilder sql = new StringBuilder("SELECT * FROM room WHERE 1=1");
	    List<Object> params = new java.util.ArrayList<>();

	    if (cinemaId != null) {
	        sql.append(" AND cinema_id = ?");
	        params.add(cinemaId);
	    }

	    if (status != null) {
	        sql.append(" AND status = ?");
	        params.add(status);
	    }

	    try {
	        return jdbc.query(sql.toString(), new RoomMapper(), params.toArray());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return List.of();
	    }
	}

	// ================= FIND BY ID =================
	public Room findById(int id) {

		String sql = "SELECT * FROM room WHERE id = ?";

		try {
			List<Room> list = jdbc.query(sql, new RoomMapper(), id);

			if (list.isEmpty()) {
				return null;
			}

			return list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// ================= INSERT =================
	public int insert(Room r) {

		String sql = """
				    INSERT INTO room(cinema_id, name, type, total_seats, status, seat_layout, created_at)
				    VALUES (?, ?, ?, ?, ?,?, NOW())
				""";

		try {
			return jdbc.update(sql, r.getCinemaId(), r.getName(), r.getType(), r.getTotalSeats(), r.getStatus(),
					r.getSeatLayout());
		} catch (Exception e) {
			e.printStackTrace();
			return 0; // Return 0 on error
		}
	}

	// ================= UPDATE =================
	public int update(int id, Room r) {

		String sql = """
				    UPDATE room
				    SET name = ?, type = ?, total_seats = ?, status = ?, seat_layout = ?
				    WHERE id = ?
				""";

		return jdbc.update(sql, r.getName(), r.getType(), r.getTotalSeats(), r.getStatus(), r.getSeatLayout(), id);
	}

	// ================= UPDATE SEAT LAYOUT =================
	public int updateSeatLayout(int id, String seatLayout, int totalSeats) {

		String sql = """
				    UPDATE room
				    SET seat_layout = ?, total_seats = ?
				    WHERE id = ?
				""";

		return jdbc.update(sql, seatLayout, totalSeats, id);
	}

	// ================= DELETE =================
	public int delete(int id) {

		String sql = "DELETE FROM room WHERE id = ?";

		try {
			return jdbc.update(sql, id);
		} catch (Exception e) {
			e.printStackTrace();
			return 0; // Return 0 on error
		}
	}

	// ====== UPDATE STATUS ======
	public int updateStatus(int id, int status) {
		String sql = "UPDATE room SET status = ? WHERE id = ?";
		try {
			return jdbc.update(sql, status, id);
		} catch (Exception e) {
			e.printStackTrace();
			return 0; // Return 0 on error
		}
	}
}