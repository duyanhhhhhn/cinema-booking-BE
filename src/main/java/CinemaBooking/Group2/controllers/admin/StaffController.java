package CinemaBooking.Group2.controllers.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.staff.staffScheduleResponseDTO;
import CinemaBooking.Group2.dtos.staff.workShiftResponseDTO;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;
import CinemaBooking.Group2.service.StaffScheduleService;

@RestController
@RequestMapping("/admin/api/staff")
public class StaffController {
	@Autowired
	private StaffScheduleService service;
	@CrossOrigin
	@PostMapping("/assign")
	public String staffAssign(@RequestParam("staff_id") int staff_id,
			@RequestParam("shift_id")int shift_id,
			@RequestParam("work_date")LocalDate work_date,
			@RequestParam("status") StaffScheduleStatus status) {
		String m="error";
		try {
			StaffSchedule item = new StaffSchedule();
			item.setStaffId(staff_id);
			item.setShiftId(shift_id);
			item.setWorkDate(work_date);
			item.setStatus(status);
			if(service.assignStaff(item)==1) {
				m="success";
			}
			else {
				m="error";
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return m;
	}
	@GetMapping("api/staff/schedules")
	public List<staffScheduleResponseDTO> getSchedules(){
		try {
			return service.getSchedule();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	@GetMapping("api/staff/shifts")
	public List<workShiftResponseDTO> getShift(){
		try {
			return service.getShift();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
