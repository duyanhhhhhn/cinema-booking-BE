package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import CinemaBooking.Group2.models.RefreshToken;

public class RefreshTokenMapper implements RowMapper<RefreshToken> {

    @Override
    public RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        RefreshToken r = new RefreshToken();

        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setToken(rs.getString("token"));

        if (rs.getTimestamp("issued_at") != null)
            r.setIssuedAt(rs.getTimestamp("issued_at").toLocalDateTime());

        if (rs.getTimestamp("expires_at") != null)
            r.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());

        r.setRevoked(rs.getInt("revoked"));

        if (rs.getTimestamp("revoked_at") != null)
            r.setRevokedAt(rs.getTimestamp("revoked_at").toLocalDateTime());

        // Optional fields (nullable)
        r.setIpAddress(rs.getString("ip_address"));
        r.setUserAgent(rs.getString("user_agent"));

        return r;
    }
}
