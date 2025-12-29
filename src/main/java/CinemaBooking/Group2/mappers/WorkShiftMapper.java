package CinemaBooking.Group2.mappers;

import CinemaBooking.Group2.dtos.staff.workShiftResponseDTO;
import CinemaBooking.Group2.models.WorkShift;

public class WorkShiftMapper {
	public static workShiftResponseDTO toResponseDTO(WorkShift shift) {
		if(shift==null) {
			return null;
		}
		workShiftResponseDTO item = new workShiftResponseDTO();
		item.setId(shift.getId());
		item.setName(shift.getName());
		item.setStartTime(shift.getStartTime());
		item.setEndTime(shift.getEndTime());
		item.setCreatedAt(shift.getCreatedAt());
		return item;
	}
}
