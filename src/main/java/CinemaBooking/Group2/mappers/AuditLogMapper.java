package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.dtos.dashboardnreports.auditLogResponseDTO;
import CinemaBooking.Group2.models.AuditLog;

public class AuditLogMapper implements RowMapper<AuditLog>{
	public static auditLogResponseDTO toResponseDTO(AuditLog log) {
		if(log ==null) {
			return null;
		}
		try {
			auditLogResponseDTO item = new auditLogResponseDTO();
			item.setId(log.getId());
			item.setAction(log.getAction());
			item.setDetails(log.getDetails());
			item.setIpAddress(log.getIpAddress());
			item.setResourceId(log.getResourceId());
			item.setResourceType(log.getResourceType());
			item.setUserAgent(log.getUserAgent());
			item.setUserId(log.getUserId());
			item.setCreatedAt(log.getCreatedAt());
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	@Override
	public AuditLog mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		AuditLog item = new AuditLog();
		item.setId(rs.getInt("id"));
		item.setUserId(rs.getInt("user_id"));
		item.setAction(rs.getString("action"));
		item.setResourceId(rs.getString("resource_id"));
		item.setResourceType(rs.getString("resource_type"));
		item.setDetails(rs.getString("details"));
		item.setIpAddress(rs.getString("ip_address"));
		item.setUserAgent(rs.getString("user_agent"));
		item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		return item;
	}
}
