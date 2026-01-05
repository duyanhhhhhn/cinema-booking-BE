package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.dashboardnreports.auditLogResponseDTO;
import CinemaBooking.Group2.mappers.AuditLogMapper;
import CinemaBooking.Group2.models.AuditLog;
import CinemaBooking.Group2.pattern.DashboardNReport;

@Service
public class DashboardService {
	public List<auditLogResponseDTO> getAuditLog(){
		try {
			List<AuditLog> item = DashboardNReport.Instance().getAuditLog();
			return item.stream().map(AuditLogMapper::toResponseDTO).collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public BigDecimal getRevenueByMonth(int month){
		BigDecimal rs = new BigDecimal(0);
		try {
			rs = DashboardNReport.Instance().getRevenueByMonth(month);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	public BigDecimal getRevenueByDate(LocalDate date){
		BigDecimal rs = new BigDecimal(0);
		try {
			rs = DashboardNReport.Instance().getRevenueByDate(date);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return rs;
	}
}
