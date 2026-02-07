package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.models.Room;

public class RoomMapper implements RowMapper<Room> {

    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {

        Room r = new Room();

        r.setId(rs.getInt("id"));
        r.setCinemaId(rs.getInt("cinema_id"));
        r.setName(rs.getString("name"));
        r.setType(rs.getString("type"));
        r.setTotalSeats(rs.getInt("total_seats"));
        r.setSeatLayout(rs.getString("seat_layout"));

        if (rs.getTimestamp("created_at") != null) {
            r.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime()
            );
        }

        return r;
    }
}


