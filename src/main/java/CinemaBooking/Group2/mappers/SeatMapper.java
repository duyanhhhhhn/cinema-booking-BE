package CinemaBooking.Group2.mappers;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.models.Seat;

public class SeatMapper implements RowMapper<Seat> {

    @Override
    public Seat mapRow(ResultSet rs, int i)
            throws SQLException {

        Seat s = new Seat();

        s.setId(rs.getInt("id"));
        s.setRoomId(rs.getInt("room_id"));
        s.setSeatCode(rs.getString("seat_code"));

        s.setSeatType(
            Seat.SeatType.valueOf(
                rs.getString("seat_type")
            )
        );

        s.setExtraPrice(
            rs.getBigDecimal("extra_price")
        );

        return s;
    }
}

