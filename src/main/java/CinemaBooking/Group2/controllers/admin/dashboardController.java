package CinemaBooking.Group2.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.dashboardnreports.auditLogResponseDTO;
import CinemaBooking.Group2.service.DashboardService;

@RestController
@RequestMapping("/admin/api")
public class dashboardController {
	@Autowired
	private DashboardService service;
	@RequestMapping("/audit-logs")
	@CrossOrigin
	public List<auditLogResponseDTO> getLog(){
		try {
			return service.getAuditLog();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
