package CinemaBooking.Group2.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.admin.CreateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UpdateUserRequestDTO;
import CinemaBooking.Group2.dtos.admin.UserResponseDTO;
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

	// tạo staff mới
	public int createStaff(CreateUserRequestDTO u, String hashedPassword) {
		String sql = """
				    INSERT INTO user (
				        role_id, cinema_id, position,
				        full_name, email, password, phone, is_active
				    )
				    VALUES (?, ?, ?, ?, ?, ?, ?, 1)
				""";

		try {
			return jdbc.update(sql, u.getRoleId(), u.getCinemaId(), u.getPosition(), u.getFullName(), u.getEmail(),
					hashedPassword, u.getPhone());
		} catch (Exception e) {
			return 0;
		}
	}
	
	// Lấy thông tin user 
	public List<UserResponseDTO> findUsers(Integer roleId, Integer cinemaId) {

        String sql = """
            SELECT 
                id, full_name, email, phone,
                role_id, cinema_id, position, is_active
            FROM user
            WHERE 1=1
        """;

        List<Object> params = new ArrayList<>();

        try {
			if (roleId != null) {
				sql += " AND role_id = ?";
				params.add(roleId);
			}

			if (cinemaId != null) {
				sql += " AND cinema_id = ?";
				params.add(cinemaId);
			}

			return jdbc.query(sql, params.toArray(), (rs, rowNum) -> {
	            UserResponseDTO dto = new UserResponseDTO();
	            dto.setId(rs.getInt("id"));
	            dto.setFullName(rs.getString("full_name"));
	            dto.setEmail(rs.getString("email"));
	            dto.setPhone(rs.getString("phone"));
	            dto.setRoleId(rs.getInt("role_id"));
	            dto.setCinemaId(rs.getInt("cinema_id"));
	            dto.setPosition(rs.getString("position"));
	            dto.setIsActive(rs.getInt("is_active"));
	            return dto;
	        });
		} catch (Exception e) {
			return new ArrayList<>();
		}
    }
	
	public void updateUser(int id, UpdateUserRequestDTO req) {

	    StringBuilder sql = new StringBuilder("UPDATE user SET ");
	    List<Object> params = new ArrayList<>();

	    try {
	    	if (req.getFullName() != null) {
	            sql.append("full_name = ?, ");
	            params.add(req.getFullName());
	        }

	        if (req.getPhone() != null) {
	            sql.append("phone = ?, ");
	            params.add(req.getPhone());
	        }

	        if (req.getAvatarUrl() != null) {
	            sql.append("avatar_url = ?, ");
	            params.add(req.getAvatarUrl());
	        }

	        if (req.getIsActive() != null) {
	            sql.append("is_active = ?, ");
	            params.add(req.getIsActive());
	        }

	        if (params.isEmpty()) {
	            return; // không có gì để update
	        }

	        sql.setLength(sql.length() - 2); // bỏ ", "
	        sql.append(" WHERE id = ?");
	        params.add(id);

	        jdbc.update(sql.toString(), params.toArray());
	    }
	    catch (Exception e) {
	    	throw new RuntimeException("Lỗi khi cập nhật user: " + e.getMessage());
	    }
	}
	
	public UserResponseDTO findUserDetail(int id) {
	    String sql = """
	        SELECT id, full_name, email, phone, role_id, cinema_id, position, is_active
	        FROM user WHERE id = ?
	    """;

	    try {
	        return jdbc.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
	            UserResponseDTO dto = new UserResponseDTO();
	            dto.setId(rs.getInt("id"));
	            dto.setFullName(rs.getString("full_name"));
	            dto.setEmail(rs.getString("email"));
	            dto.setPhone(rs.getString("phone"));
	            dto.setRoleId(rs.getInt("role_id"));
	            dto.setCinemaId(rs.getInt("cinema_id"));
	            dto.setPosition(rs.getString("position"));
	            dto.setIsActive(rs.getInt("is_active"));
	            return dto;
	        });
	    } catch (Exception e) {
	        return null;
	    }
	}


}
