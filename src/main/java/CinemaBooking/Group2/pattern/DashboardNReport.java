package CinemaBooking.Group2.pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import CinemaBooking.Group2.models.AuditLog;
import CinemaBooking.Group2.repositories.AuditLogRepository;
import CinemaBooking.Group2.repositories.PaymentRepository;

public class DashboardNReport {
	private static DashboardNReport _instance = null;
	private AuditLogRepository logRep;
	private PaymentRepository payRep;
	private DashboardNReport() {
		logRep = AuditLogRepository.Instance();
		payRep = PaymentRepository.Instance();
	}
	public static DashboardNReport Instance () {
		if(_instance==null) {
			_instance=new DashboardNReport();
		}
		return _instance;
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
