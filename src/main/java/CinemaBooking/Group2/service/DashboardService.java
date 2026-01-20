package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.dashboardnreports.AuditLogListResponseDTO;
import CinemaBooking.Group2.dtos.dashboardnreports.auditLogResponseDTO;
import CinemaBooking.Group2.models.AuditLog;
import CinemaBooking.Group2.pattern.DashboardNReport;

@Service
public class DashboardService {
	@Autowired private DashboardNReport dashboard;
	public AuditLogListResponseDTO getAuditLog(){
		AuditLogListResponseDTO audit = new AuditLogListResponseDTO();
		try {
			
			List<AuditLog> item = dashboard.getAuditLog();
			if(item!=null) {
				audit.setLog(item);
				audit.setMessage("Success");
				audit.setSuccess(true);
			}
			else {
				audit.setMessage("Error");
				audit.setSuccess(false);
			}
			return audit;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public auditLogResponseDTO writeLog(AuditLog log,int uid) {
		auditLogResponseDTO item = new auditLogResponseDTO();
		try {
			if(log==null) {
				item.setMessage("Log is null");
				item.setSuccess(false);
			}
			else if(uid==0){
				item.setMessage("");
				item.setMessage("uid shouldn't be 0");
				item.setSuccess(false);
			}
			else {
				item.setMessage("Success");
				item.setSuccess(true);
			}
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public BigDecimal getRevenueByMonth(int month){
		BigDecimal rs = new BigDecimal(0);
		try {
			rs = dashboard.getRevenueByMonth(month);
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
			rs = dashboard.getRevenueByDate(date);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return rs;
	}
}
