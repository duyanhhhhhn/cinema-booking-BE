package CinemaBooking.Group2.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.staff.ScheduleResponseDTO;
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
	public List<staffScheduleResponseDTO> getSchedule(int page,int size){
		try {
			List<StaffSchedule> item = pattern.getSchedule(page,size);
			return item.stream().map(StaffMapper::toResponseDTO).collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	public List<ScheduleResponseDTO> getThisWeekSchedule(LocalDate date){
		try {
			List<ScheduleResponseDTO> list = new ArrayList<>();
			LocalDate monday =date.with(DayOfWeek.MONDAY);
			for(int i =0;i<7;i++) {
				LocalDate date1 = monday.plusDays(i);
				StaffSchedule item = pattern.getScheduleByDate(date1);
				ScheduleResponseDTO dto = new ScheduleResponseDTO();
				if(item!=null) {
					dto.setId(item.getId());
					dto.setStaff(pattern.findStaffById(item.getStaffId()));
					dto.setShift(pattern.findByShiftId(item.getShiftId()));
					dto.setStatus(item.getStatus());
					dto.setWorkdate(date1);
					list.add(dto);
				}
			}
			
			return list;
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
