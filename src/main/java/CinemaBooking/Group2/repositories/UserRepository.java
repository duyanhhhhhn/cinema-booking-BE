package CinemaBooking.Group2.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.auth.RegisterRequestDTO;
import CinemaBooking.Group2.models.User;

@Repository
public class UserRepository {

	@Autowired
	private JdbcTemplate jdbc;

	public boolean existsByEmail(String email) {
		String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
		Integer count = jdbc.queryForObject(sql, Integer.class, email);
		return count != null && count > 0;
	}

	public int createUser(RegisterRequestDTO u, String hashedPassword) {
		String sql = """
				    INSERT INTO user (role_id, full_name, email, password, phone, is_active)
				    VALUES (4, ?, ?, ?, ?, 1)
				""";

		jdbc.update(sql, u.getFullName(), u.getEmail(), hashedPassword, u.getPhone());

		return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
	}

	public User findByEmail(String email) {
		String sql = "SELECT * FROM user WHERE email = ?";
		var list = jdbc.query(sql, new CinemaBooking.Group2.mappers.UserMapper(), email);
		return list.isEmpty() ? null : list.get(0);
	}
}
