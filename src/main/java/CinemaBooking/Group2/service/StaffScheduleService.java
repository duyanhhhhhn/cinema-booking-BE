package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.staff.staffScheduleResponseDTO;
import CinemaBooking.Group2.dtos.staff.workShiftResponseDTO;
import CinemaBooking.Group2.mappers.StaffMapper;
import CinemaBooking.Group2.mappers.WorkShiftMapper;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.WorkShift;
import CinemaBooking.Group2.pattern.ModelMaker;

@Service
public class StaffScheduleService {
	public List<staffScheduleResponseDTO> getSchedule(){
		try {
			List<StaffSchedule> item = ModelMaker.Instance().getSchedule();
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
			return ModelMaker.Instance().assignStaff(item);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public List<workShiftResponseDTO> getShift() {
		try {
			List<WorkShift> item = ModelMaker.Instance().getShift();
			return item.stream().map(WorkShiftMapper::toResponseDTO).collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
