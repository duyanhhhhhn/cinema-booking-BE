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
        return jdbc.query(sql, new RoomMapper());
    }
    // ================= FIND BY CINEMA =================
    public List<Room> findByCinema(int cinemaId) {

        String sql = "SELECT * FROM room WHERE cinema_id = ?";

        return jdbc.query(sql, new RoomMapper(), cinemaId);
    }

    // ================= FIND BY ID =================
    public Room findById(int id) {

        String sql = "SELECT * FROM room WHERE id = ?";

        List<Room> list = jdbc.query(sql, new RoomMapper(), id);

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    // ================= INSERT =================
    public int insert(Room r) {

        String sql = """
            INSERT INTO room(cinema_id, name, type, total_seats, seat_layout, created_at)
            VALUES (?, ?, ?, ?, ?, NOW())
        """;

        return jdbc.update(
                sql,
                r.getCinemaId(),
                r.getName(),
                r.getType(),
                r.getTotalSeats(),
                r.getSeatLayout()
        );
    }

    // ================= UPDATE =================
    public int update(int id, Room r) {

        String sql = """
            UPDATE room
            SET name = ?, type = ?, total_seats = ?, seat_layout = ?
            WHERE id = ?
        """;

        return jdbc.update(
                sql,
                r.getName(),
                r.getType(),
                r.getTotalSeats(),
                r.getSeatLayout(),
                id
        );
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

        return jdbc.update(sql, id);
    }
}