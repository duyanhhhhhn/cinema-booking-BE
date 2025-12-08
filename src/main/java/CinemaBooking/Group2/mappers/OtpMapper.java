package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.models.Otp;
import CinemaBooking.Group2.models.Otp.OtpPurpose;

public class OtpMapper implements RowMapper<Otp> {

    @Override
    public Otp mapRow(ResultSet rs, int rowNum) throws SQLException {
        Otp otp = new Otp();

        otp.setId(rs.getInt("id"));
        otp.setUserId((Integer) rs.getObject("user_id")); // Cho phép null
        otp.setEmail(rs.getString("email"));
        otp.setCode(rs.getString("code"));

        // ENUM purpose -> dùng name() của DB
        String purposeStr = rs.getString("purpose");
        otp.setPurpose(OtpPurpose.valueOf(purposeStr)); // DB enum đã chuẩn

        // Timestamp nullable
        var expires = rs.getTimestamp("expires_at");
        if (expires != null) {
            otp.setExpiresAt(expires.toLocalDateTime());
        }

        otp.setUsed(rs.getInt("is_used")); // 0/1 từ DB (bạn đang dùng int nên OK)

        var created = rs.getTimestamp("created_at");
        if (created != null) {
            otp.setCreatedAt(created.toLocalDateTime());
        }

        return otp;
    }
}
