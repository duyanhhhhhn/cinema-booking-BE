package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.models.Cinema;

public class CinemaMapper implements RowMapper<Cinema> {
    @Override
    public Cinema mapRow(ResultSet rs, int rowNum) throws SQLException {
        Cinema c = new Cinema();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setAddress(rs.getString("address"));
        c.setPhone(rs.getString("phone"));
        c.setDescription(rs.getString("description"));
        c.setIsActive(rs.getInt("is_active"));
        c.setImageUrl(rs.getString("image_url"));
        c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return c;
    }
}
