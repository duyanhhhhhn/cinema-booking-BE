package CinemaBooking.Group2.repositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.OtpMapper;
import CinemaBooking.Group2.models.Otp;

@Repository
public class OtpRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public void save(Otp otp) {
        String sql = """
            INSERT INTO otp(email, code, purpose, expires_at)
            VALUES (?, ?, ?, ?)
        """;
        jdbc.update(sql, otp.getEmail(), otp.getCode(), otp.getPurpose().name(), otp.getExpiresAt());
    }

    public Optional<Otp> findLatestValid(String email, Otp.OtpPurpose purpose) {
        String sql = """
            SELECT * FROM otp
            WHERE email = ?
              AND purpose = ?
              AND is_used = 0
              AND expires_at > NOW()
            ORDER BY id DESC
            LIMIT 1
        """;
        var list = jdbc.query(sql, new OtpMapper(), email, purpose.name());
        return list.stream().findFirst();
    }

    public void attachUserAndMarkUsed(int otpId, int userId) {
        String sql = "UPDATE otp SET is_used = 1, user_id = ? WHERE id = ?";
        jdbc.update(sql, userId, otpId);
    }
}
