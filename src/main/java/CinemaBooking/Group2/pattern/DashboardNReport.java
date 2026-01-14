package CinemaBooking.Group2.pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.AuditLog;
import CinemaBooking.Group2.repositories.AuditLogRepository;
import CinemaBooking.Group2.repositories.PaymentRepository;

@Repository
public class DashboardNReport {
	@Autowired
	private AuditLogRepository logRep;
	@Autowired
	private PaymentRepository payRep;
	public DashboardNReport() {
	}
	//Audit Log
	public List<AuditLog> getAuditLog(){
		try {
			return logRep.getAll();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public int addAuditLog(AuditLog item,int uid) {
		try {
			item.setUserId(uid);
			return logRep.create(item);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}
	//Payment
	public BigDecimal getRevenueByMonth(int month) {
		BigDecimal rs = new BigDecimal(0);
		try {
			rs = payRep.getRevenueByMonth(month);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return new BigDecimal(0);
	}
	public BigDecimal getRevenueByDate(LocalDate date) {
		BigDecimal rs = new BigDecimal(0);
		try {
			rs = payRep.getRevenueByDay(date);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return new BigDecimal(0);
	}
}
