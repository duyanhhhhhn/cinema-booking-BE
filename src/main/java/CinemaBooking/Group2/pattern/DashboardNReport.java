package CinemaBooking.Group2.pattern;

import java.util.List;

import CinemaBooking.Group2.models.AuditLog;
import CinemaBooking.Group2.repositories.AuditLogRepository;

public class DashboardNReport {
	private static DashboardNReport _instance = null;
	private AuditLogRepository logRep;
	private DashboardNReport() {
		logRep = AuditLogRepository.Instance();
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
}
