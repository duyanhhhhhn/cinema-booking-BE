package CinemaBooking.Group2.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.RefreshTokenMapper;
import CinemaBooking.Group2.models.RefreshToken;

@Repository
public class RefreshTokenRepository {

	@Autowired
	private JdbcTemplate jdbc;

	/**
	 * Save refresh token mới
	 */
	public int saveToken(int userId, String token) {
		String sql = """
				    INSERT INTO refresh_token (user_id, token, issued_at, expires_at, revoked)
				    VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 0)
				""";

		KeyHolder kh = new GeneratedKeyHolder();
		try {
			jdbc.update(connection -> {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, userId);
				ps.setString(2, token);
				return ps;
			}, kh);
			return kh.getKey().intValue();
		} catch (Exception ex) {
			return -1;
		}
	}

	/**
	 * Lấy refresh token để kiểm tra
	 */
	public Optional<RefreshToken> findByToken(String token) {
		String sql = "SELECT * FROM refresh_token WHERE token = ?";
		try {
			var list = jdbc.query(sql, new RefreshTokenMapper(), token);
			return list.stream().findFirst();
		} catch (Exception ex) {
			return Optional.empty();
		}
	}

	/**
	 * Revoke 1 token
	 */
	public void revoke(String token) {
		String sql = "UPDATE refresh_token SET revoked = 1, revoked_at = NOW() WHERE token = ?";
		try {
			jdbc.update(sql, token);
		} catch (Exception ex) {
			// Ignore
		}
	}

	/**
	 * Khi Refresh Token hợp lệ → chưa revoke và chưa hết hạn
	 */
	public boolean isValid(String token) {
		String sql = """
				    SELECT COUNT(*) FROM refresh_token
				    WHERE token = ? AND revoked = 0 AND expires_at > NOW()
				""";
		try {
			Integer count = jdbc.queryForObject(sql, Integer.class, token);
			return count != null && count > 0;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Logout tất cả thiết bị của user
	 */
	public void revokeAllForUser(int userId) {
		String sql = "UPDATE refresh_token SET revoked = 1, revoked_at = NOW() WHERE user_id = ? AND revoked = 0";
		try {
			jdbc.update(sql, userId);
		} catch (Exception ex) {
			// Ignore
		}
	}
}
