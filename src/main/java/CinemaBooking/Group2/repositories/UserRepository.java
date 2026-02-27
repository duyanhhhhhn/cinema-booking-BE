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
import CinemaBooking.Group2.dtos.auth.UserDTO;
import CinemaBooking.Group2.mappers.UserMapper;
import CinemaBooking.Group2.models.User;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbc;

    // ===================== EXIST =====================
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, email);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // ===================== CREATE =====================
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

    public int createStaff(CreateUserRequestDTO u, String hashedPassword) {
        String sql = """
                INSERT INTO user (
                    role_id, cinema_id, position,
                    full_name, email, password, phone, avatar_url, is_active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)
            """;

        try {
            return jdbc.update(sql, u.getRoleId(), u.getCinemaId(), u.getPosition(),
                    u.getFullName(), u.getEmail(), hashedPassword, u.getPhone(), u.getAvatarUrl());
        } catch (Exception e) {
            return 0;
        }
    }

    // ===================== FIND =====================
    public User findByEmail(String email) {
        String sql = """
                SELECT u.*, r.name AS role_name
                FROM user u
                JOIN role r ON u.role_id = r.id
                WHERE u.email = ?
            """;
        try {
            return jdbc.queryForObject(sql, new UserMapper(), email);
        } catch (Exception e) {
            return null;
        }
    }

    public User findById(int id) {
        String sql = "SELECT u.*, r.name AS role_name FROM user u JOIN role r ON u.role_id = r.id WHERE u.id = ?";
        try {
            return jdbc.queryForObject(sql, new UserMapper(), id);
        } catch (Exception e) {
            return null;
        }
    }

    public UserResponseDTO findUserDetail(int id) {
        String sql = """
                SELECT id, full_name, email, phone, role_id, cinema_id, position, is_active
                FROM user
                WHERE id = ?
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

    public List<UserResponseDTO> findUsers(Integer roleId, Integer cinemaId) {
        String sql = """
                SELECT id, full_name, email, phone, role_id, cinema_id, position, is_active
                FROM user
                WHERE 1=1
            """;

        List<Object> params = new ArrayList<>();

        if (roleId != null) {
            sql += " AND role_id = ?";
            params.add(roleId);
        }

        if (cinemaId != null) {
            sql += " AND cinema_id = ?";
            params.add(cinemaId);
        }

        try {
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

    // ===================== UPDATE =====================
    public void updateUser(int id, UpdateUserRequestDTO req) {
        StringBuilder sql = new StringBuilder("UPDATE user SET ");
        List<Object> params = new ArrayList<>();

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

        if (params.isEmpty()) return;

        sql.setLength(sql.length() - 2); // remove last ", "
        sql.append(" WHERE id = ?");
        params.add(id);

        try {
            jdbc.update(sql.toString(), params.toArray());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật user: " + e.getMessage());
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

    public void updateProfile(int userId, String fullName, String phone) {
        StringBuilder sql = new StringBuilder("UPDATE user SET ");
        List<Object> params = new ArrayList<>();

        if (fullName != null) {
            sql.append("full_name = ?, ");
            params.add(fullName);
        }
        if (phone != null) {
            sql.append("phone = ?, ");
            params.add(phone);
        }
        if (params.isEmpty()) return;

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(userId);

        try {
            jdbc.update(sql.toString(), params.toArray());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật profile: " + e.getMessage());
        }
    }

    public void updateAvatar(int userId, String avatarUrl) {
        String sql = "UPDATE user SET avatar_url = ? WHERE id = ?";
        try {
            jdbc.update(sql, avatarUrl, userId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật avatar: " + e.getMessage());
        }
    }

    // ===================== LOCK / UNLOCK =====================
    public void unlockUser(int id) {
        String sql = "UPDATE user SET is_active = 1 WHERE id = ?";
        try {
            jdbc.update(sql, id);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi mở khóa user: " + e.getMessage());
        }
    }

    public void lockUser(int id) {
        String sql = "UPDATE user SET is_active = 0 WHERE id = ?";
        try {
            jdbc.update(sql, id);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi khóa user: " + e.getMessage());
        }
    }

    // ===================== PAGINATION =====================
    public List<UserDTO> findPagedUsers(
            List<Integer> roleIds,
            Integer cinemaId,
            String search,
            int offset,
            int limit
    ) {

        try {

            String sql = """
                SELECT
                    u.*,
                    r.name AS role_name
                FROM user u
                JOIN role r ON u.role_id = r.id
                WHERE u.role_id IN (%s)
            """;

            String inSql = roleIds.stream()
                    .map(r -> "?")
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            sql = String.format(sql, inSql);

            List<Object> params = new ArrayList<>(roleIds);

            // Filter cinema
            if (cinemaId != null) {
                sql += " AND u.cinema_id = ?";
                params.add(cinemaId);
            }

            // Search
            if (search != null && !search.isBlank()) {
                sql += " AND (u.full_name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?)";
                String keyword = "%" + search + "%";
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
            }

            sql += " ORDER BY u.created_at DESC LIMIT ? OFFSET ?";
            params.add(limit);
            params.add(offset);

            return jdbc.query(sql, params.toArray(), (rs, i) ->
                new UserDTO(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("avatar_url"),
                    rs.getString("role_name"),   
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getObject("cinema_id", Integer.class),
                    rs.getString("position"),
                    rs.getInt("is_active")
                )
            );

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách user: " + e.getMessage(), e);
        }
    }


    public int countUsers(List<Integer> roleIds, Integer cinemaId, String search) {
        String sql = """
                SELECT COUNT(*)
                FROM user u
                WHERE u.role_id IN (%s)
            """;

        String inSql = roleIds.stream().map(r -> "?").reduce((a, b) -> a + "," + b).orElse("");
        sql = String.format(sql, inSql);

        List<Object> params = new ArrayList<>(roleIds);

        if (cinemaId != null) {
            sql += " AND u.cinema_id = ?";
            params.add(cinemaId);
        }

        if (search != null && !search.isBlank()) {
            sql += " AND (u.full_name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?)";
            String keyword = "%" + search + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }

        try {
            return jdbc.queryForObject(sql, params.toArray(), Integer.class);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm số lượng user: " + e.getMessage());
        }
    }

    // ===================== SAVE =====================
    public int save(User user) {
        String sql = """
                INSERT INTO user (role_id, full_name, email, password, phone, is_active)
                VALUES (?, ?, ?, ?, ?, ?)
            """;

        try {
            return jdbc.update(sql, user.getRoleId(), user.getFullName(), user.getEmail(),
                    user.getPassword(), user.getPhone(), user.getIsActive());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}