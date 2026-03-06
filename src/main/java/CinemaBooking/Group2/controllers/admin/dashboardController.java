package CinemaBooking.Group2.controllers.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.dashboardnreports.AuditLogListResponseDTO;
import CinemaBooking.Group2.dtos.dashboardnreports.auditLogResponseDTO;
import CinemaBooking.Group2.dtos.dashboardnreports.revenueResponseDTO;
import CinemaBooking.Group2.models.AuditLog;
import CinemaBooking.Group2.models.Enum.Status;
import CinemaBooking.Group2.service.DashboardService;

@RestController
@RequestMapping("/api")
public class dashboardController {
	@Autowired
	private DashboardService service;
	@GetMapping("/audit-logs")
	@CrossOrigin
	public ResponseEntity<AuditLogListResponseDTO> getLog(){
		AuditLogListResponseDTO response = new AuditLogListResponseDTO();
		try {
			 if(service.getAuditLog().getLog()==null) {
				 response.setMessage("Not found");
				 response.setSuccess(false);
				 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			 }
			 else {
				 return ResponseEntity.ok(response);
			 }
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	@PostMapping("/audit-logs/write")
	@CrossOrigin
	public ResponseEntity<auditLogResponseDTO> writeLog(AuditLog item, int uid){
		auditLogResponseDTO audit = new auditLogResponseDTO();
		try {
			auditLogResponseDTO rs =service.writeLog(item, uid);
			if(service.writeLog(item, uid)==null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(audit);
			}
			else if(!rs.isSuccess()){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(audit);
			}
			else {
				return ResponseEntity.ok(audit);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(audit);
	}
	@GetMapping("/revenue/month")
	@CrossOrigin
	public ResponseEntity<revenueResponseDTO> getRevenueByMonth(@RequestParam(name="month",defaultValue = "1") int month){
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
	@GetMapping("/revenue/week")
	@CrossOrigin
	public ResponseEntity<ApiResponse<List<revenueResponseDTO>>> getRevenueByWeek(@RequestParam("date") LocalDate firstDate){
		try {
			ApiResponse<List<revenueResponseDTO>> res;
			List<revenueResponseDTO> list = service.getAWeekOfRevenue(firstDate);
			if(list!=null) {
				res = new ApiResponse<List<revenueResponseDTO>>("Success", list);
				return ResponseEntity.ok(res);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
