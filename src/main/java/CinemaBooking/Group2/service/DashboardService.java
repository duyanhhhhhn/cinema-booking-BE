package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.dashboardnreports.auditLogResponseDTO;
import CinemaBooking.Group2.mappers.AuditLogMapper;
import CinemaBooking.Group2.models.AuditLog;
import CinemaBooking.Group2.pattern.ModelMaker;

@Service
public class DashboardService {
	public List<auditLogResponseDTO> getAuditLog(){
		try {
			List<AuditLog> item = ModelMaker.Instance().getAuditLog();
			return item.stream().map(AuditLogMapper::toResponseDTO).collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
