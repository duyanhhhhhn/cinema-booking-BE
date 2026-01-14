package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.staff.staffScheduleResponseDTO;
import CinemaBooking.Group2.dtos.staff.workShiftResponseDTO;
import CinemaBooking.Group2.mappers.StaffMapper;
import CinemaBooking.Group2.mappers.WorkShiftMapper;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.WorkShift;
import CinemaBooking.Group2.pattern.StaffSchedulePattern;

@Service
public class StaffScheduleService {
	@Autowired
	private StaffSchedulePattern pattern;
	public List<staffScheduleResponseDTO> getSchedule(){
		try {
			List<StaffSchedule> item = pattern.getSchedule();
			return item.stream().map(StaffMapper::toResponseDTO).collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	public List<staffScheduleResponseDTO> getScheduleByStaffId(int id){
		try {
			List<StaffSchedule> item = pattern.getScheduleByStaffId(id);
			return item.stream().map(StaffMapper::toResponseDTO).collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	public int assignStaff(StaffSchedule item) {
		try {
			if(item==null) {
				return 0;
			}
			return pattern.assignStaff(item);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public List<workShiftResponseDTO> getShift() {
		try {
			List<WorkShift> item = pattern.getShift();
			return item.stream().map(WorkShiftMapper::toResponseDTO).collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
