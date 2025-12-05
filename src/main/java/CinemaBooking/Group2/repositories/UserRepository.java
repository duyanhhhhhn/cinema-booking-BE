package CinemaBooking.Group2.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.User;


@Repository
public class UserRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public boolean checkUserExistsByEmail(String email) {
		String sql = "SELECT COUNT(*) FROM `user` WHERE email = ?";
		try {
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
			return count != null && count > 0;
		} catch (Exception e) {
			System.out.println("Error checking user existence: " + e.getMessage());
		}
		return false;
	}
	
	public boolean addUser(User user) {
	    String sql = "INSERT INTO `user` (role_id, cinema_id, position, full_name, email, password, phone, avatar_url, is_active, created_at, updated_at) " +
	                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    try {
	        // Kiểm tra email đã tồn tại chưa
	        if (checkUserExistsByEmail(user.getEmail())) {
	            System.out.println("User with email " + user.getEmail() + " already exists.");
	            return false;
	        }

	        // Xử lý các giá trị null cho MySQL
	        int roleId = user.getRoleId();
	        int cinemaId = user.getCinemaId();
	        String position = user.getPosition() != null ? user.getPosition().name() : null;
	        String fullName = user.getFullName() != null ? user.getFullName() : "";
	        String email = user.getEmail();
	        String password = user.getPassword() != null ? user.getPassword() : "";
	        String phone = user.getPhone() != null ? user.getPhone() : "";
	        String avatarUrl = user.getAvatarUrl() != null ? user.getAvatarUrl() : "";
	        int isActive = user.getIsActive();
	        java.sql.Timestamp createdAt = user.getCreatedAt() != null
	                ? java.sql.Timestamp.valueOf(user.getCreatedAt())
	                : new java.sql.Timestamp(System.currentTimeMillis());
	        java.sql.Timestamp updatedAt = user.getUpdatedAt() != null
	                ? java.sql.Timestamp.valueOf(user.getUpdatedAt())
	                : createdAt; // mặc định = createdAt nếu null

	        // Thực hiện insert
	        int rowsAffected = jdbcTemplate.update(sql,
	                roleId,
	                cinemaId,
	                position,
	                fullName,
	                email,
	                password,
	                phone,
	                avatarUrl,
	                isActive,
	                createdAt,
	                updatedAt
	        );

	        return rowsAffected > 0;

	    } catch (Exception e) {
	        System.out.println("Error adding user: " + e.getMessage());
	    }
	    return false;
	}
	
	public User findByEmail(String email) {
		String sql = "SELECT * FROM `user` WHERE email = ?";
		try {
			return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setRoleId(rs.getInt("role_id"));
				user.setCinemaId(rs.getInt("cinema_id"));
				String pos = rs.getString("position");
				user.setPosition(pos != null ? User.UserPosition.valueOf(pos) : null);
				user.setFullName(rs.getString("full_name"));
				user.setEmail(rs.getString("email"));
				user.setPassword(rs.getString("password"));
				user.setPhone(rs.getString("phone"));
				user.setAvatarUrl(rs.getString("avatar_url"));
				user.setIsActive(rs.getInt("is_active"));
				java.sql.Timestamp cat = rs.getTimestamp("created_at");
				java.sql.Timestamp uat = rs.getTimestamp("updated_at");
				if (cat != null) user.setCreatedAt(cat.toLocalDateTime());
				if (uat != null) user.setUpdatedAt(uat.toLocalDateTime());
				return user;
			}, email);
		} catch (Exception e) {
			System.out.println("Error retrieving user by email: " + e.getMessage());
			return null;
		}
	}
}