package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.models.User;

public class UserMapper implements RowMapper<User> {
	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setRoleId(rs.getInt("role_id"));
		user.setFullName(rs.getString("full_name"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setPhone(rs.getString("phone"));
		user.setIsActive(rs.getInt("is_active"));
		user.setCinemaId(rs.getInt("cinema_id"));
		Timestamp ts = rs.getTimestamp("created_at");
		user.setAvatarUrl(rs.getString("avatar_url"));
		if (ts != null) {
		    user.setCreatedAt(ts.toLocalDateTime());
		}

		try {
			user.setRoleName(rs.getString("role_name"));
		} catch (Exception ignored) {
					
		}

		return user;
	}
}


