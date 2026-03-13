package CinemaBooking.Group2.controllers.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.staff.ScheduleResponseDTO;
import CinemaBooking.Group2.dtos.staff.staffScheduleResponseDTO;
import CinemaBooking.Group2.dtos.staff.workShiftResponseDTO;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;
import CinemaBooking.Group2.service.StaffScheduleService;

@RestController
@RequestMapping("/api/staff")
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
	@CrossOrigin
	@GetMapping("/schedules")
	public ResponseEntity<ApiResponse<List<ScheduleResponseDTO>>> getSchedules(@RequestParam(name="page",defaultValue = "1") int page,@RequestParam(name="pageSize",defaultValue = "6") int size){
		try {
			List<ScheduleResponseDTO> schedule=service.getSchedule(page,size);
			return ResponseEntity.ok(new ApiResponse<List<ScheduleResponseDTO>>("success", schedule));
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	@CrossOrigin
	@GetMapping("/schedules/week")
	public List<ScheduleResponseDTO> getThisWeekSchedules(){
		try {
			return service.getThisWeekSchedule(LocalDate.now());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	@CrossOrigin
	@PutMapping("/schedules/{id}")
	public List<staffScheduleResponseDTO> editSchedules(@PathVariable("id")int id,@RequestParam("data") StaffSchedule data){
		try {
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	@CrossOrigin
	@GetMapping("/shifts")
	public PageResponse<workShiftResponseDTO> getShift(){
		try {
			List<workShiftResponseDTO> list = service.getShift();
			PageResponse<workShiftResponseDTO> response = new PageResponse<workShiftResponseDTO>();
			if(list!=null) {
				response.setData(list);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	@CrossOrigin
	@GetMapping("/schedule/my")
	public List<ScheduleResponseDTO> getMySchedules(@RequestParam("id")int id){
		try {
			return service.getScheduleByStaffId(id);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
