package CinemaBooking.Group2.mappers;

import CinemaBooking.Group2.dtos.staff.staffScheduleResponseDTO;
import CinemaBooking.Group2.models.StaffSchedule;

public class StaffMapper {
	public static staffScheduleResponseDTO toResponseDTO(StaffSchedule item) {
		if(item==null) {
			return null;
		}
		staffScheduleResponseDTO staff = new staffScheduleResponseDTO();
		staff.setId(item.getId());
		staff.setShiftId(item.getShiftId());
		staff.setWorkDate(item.getWorkDate());
		staff.setStatus(item.getStatus());
		staff.setStaffId(item.getStaffId());
		return staff;
	}
}
