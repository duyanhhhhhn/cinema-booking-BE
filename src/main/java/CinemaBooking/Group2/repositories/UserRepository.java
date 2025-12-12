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
		try {
			Integer count = jdbc.queryForObject(sql, Integer.class, email);
			return count != null && count > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public int createUser(RegisterRequestDTO u, String hashedPassword) {
		String sql = """
				    INSERT INTO user (role_id, full_name, email, password, phone, is_active)
				    VALUES (4, ?, ?, ?, ?, 1)
				""";

		try {
			return jdbc.update(sql, u.getFullName(), u.getEmail(), hashedPassword, u.getPhone());
		} catch (Exception e) {
			return 0;
		}
	}

	public User findByEmail(String email) {
	    String sql = """
	        SELECT u.*, r.name AS role_name 
	        FROM user u
	        JOIN role r ON u.role_id = r.id
	        WHERE u.email = ?
	    """;
	    try {
	        return jdbc.queryForObject(sql, new CinemaBooking.Group2.mappers.UserMapper(), email);
	    } catch (Exception e) {
	        return null;
	    }
	}
	public User findById(int id) {
	    String sql = "SELECT u.*, r.name AS role_name FROM user u JOIN role r ON u.role_id = r.id WHERE u.id = ?";
	    try {
	        return jdbc.queryForObject(sql, new CinemaBooking.Group2.mappers.UserMapper(), id);
	    } catch (Exception ex) {
	        return null;
	    }
	}
	
	public int updatePassword(int userId, String hashedPassword) {
	    String sql = "UPDATE user SET password = ? WHERE id = ?";
	    try {
	        return jdbc.update(sql, hashedPassword, userId);
	    } catch (Exception e) {
	        return 0;
	    }
	}

}
