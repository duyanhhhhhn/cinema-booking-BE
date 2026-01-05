package CinemaBooking.Group2.controllers.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.dashboardnreports.auditLogResponseDTO;
import CinemaBooking.Group2.dtos.dashboardnreports.revenueResponseDTO;
import CinemaBooking.Group2.models.Enum.Status;
import CinemaBooking.Group2.service.DashboardService;

@RestController
@RequestMapping("/admin/api")
public class dashboardController {
	@Autowired
	private DashboardService service;
	@GetMapping("/audit-logs")
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
	@GetMapping("/revenue/month")
	@CrossOrigin
	public ResponseEntity<revenueResponseDTO> getRevenueByMonth(@RequestParam("month") int month){
		revenueResponseDTO item = new revenueResponseDTO();
		try {
			BigDecimal re = service.getRevenueByMonth(month);
			if(re==null) {
				item.setMessage("Error when collect revenue data !");
				item.setStatus(Status.Unvailable);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(item);
			}
			else {
				item.setMessage("Success");
				item.setStatus(Status.Available);
				item.setRevenue(re);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.ok(item);
	}
	@GetMapping("/revenue/date")
	@CrossOrigin
	public ResponseEntity<revenueResponseDTO> getRevenueByMonth(@RequestParam("date") LocalDate date){
		revenueResponseDTO item = new revenueResponseDTO();
		try {
			BigDecimal re = service.getRevenueByDate(date);
			if(re==null) {
				item.setMessage("Error when collect revenue data !");
				item.setStatus(Status.Unvailable);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(item);
			}
			else {
				item.setMessage("Success");
				item.setStatus(Status.Available);
				item.setRevenue(re);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.ok(item);
	}
}
