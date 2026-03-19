package CinemaBooking.Group2.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.staff.AWeekOfScheduleResponseDTO;
import CinemaBooking.Group2.dtos.staff.ScheduleResponseDTO;
import CinemaBooking.Group2.dtos.staff.ShiftResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffResponseDTO;
import CinemaBooking.Group2.dtos.staff.workShiftResponseDTO;
import CinemaBooking.Group2.mappers.StaffMapper;
import CinemaBooking.Group2.mappers.WorkShiftMapper;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.WorkShift;
import CinemaBooking.Group2.pattern.StaffSchedulePattern;

@Service
public class StaffScheduleService {
	@Autowired
	private StaffSchedulePattern pattern;
	public List<ScheduleResponseDTO> getSchedule(int page,int size){
		try {
			List<StaffSchedule> list = pattern.getSchedule(page,size);
			List<ScheduleResponseDTO> lists = new ArrayList<>();
			for(StaffSchedule item : list) {
				ScheduleResponseDTO dto = new ScheduleResponseDTO();
				if(item!=null) {
					dto.setId(item.getId());
					User staff = pattern.findStaffById(item.getStaffId());
					StaffResponseDTO staffDTO = new StaffResponseDTO();
					staffDTO.setFullName(staff.getFullName());
					staffDTO.setAvatarUrl(staff.getAvatarUrl());
					staffDTO.setPhone(staff.getPhone());
					staffDTO.setPosition(staff.getPosition());
					dto.setStaff(staffDTO);
					dto.setShift(pattern.findByShiftId(item.getShiftId()));
					dto.setStatus(item.getStatus());
					dto.setWorkdate(item.getWorkDate());
					lists.add(dto);
				}
			}
			return lists;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	public List<AWeekOfScheduleResponseDTO> getThisWeekSchedule(LocalDate date){
		try {
			Map<Integer,AWeekOfScheduleResponseDTO>map = new HashMap<>();
			LocalDate monday =date.with(DayOfWeek.MONDAY);
			LocalDate sunday = monday.plusDays(6);
			List<StaffSchedule> schedule = pattern.getScheduleByRange(monday, sunday);
			for(StaffSchedule item :schedule) {
				int staffId = item.getStaffId();
				if(!map.containsKey(staffId)) {
					AWeekOfScheduleResponseDTO dto = new AWeekOfScheduleResponseDTO();
					User staff = pattern.findStaffById(staffId);
			        StaffResponseDTO staffDTO = new StaffResponseDTO();
			        staffDTO.setFullName(staff.getFullName());
			        staffDTO.setAvatarUrl(staff.getAvatarUrl());
			        staffDTO.setPhone(staff.getPhone());
			        staffDTO.setPosition(staff.getPosition());
			        dto.setStaff(staffDTO);
			        List<ShiftResponseDTO> week = new ArrayList<>();
			        for (int i = 0; i < 7; i++) {
			            LocalDate d = monday.plusDays(i);
			            ShiftResponseDTO empty = new ShiftResponseDTO();
			            empty.setWorkDate(d);
			            empty.setId(0);
			            week.add(empty);
			        }
			        dto.setShift(week);
			        map.put(staffId, dto);
				}
				List<ShiftResponseDTO> week = map.get(staffId).getShift();
				   for(ShiftResponseDTO day :week) {
					   if(day.getWorkDate().equals(item.getWorkDate())) {
						   day.setWorkDate(item.getWorkDate());
						    WorkShift shift = pattern.findByShiftId(item.getShiftId());
						    day.setId(shift.getId());
						    day.setName(shift.getName());
						    day.setStartTime(shift.getStartTime());
						    day.setEndTime(shift.getEndTime());
						    break;
					   }
				   }
			}
			return new ArrayList<>(map.values());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	public List<ScheduleResponseDTO> getScheduleByStaffId(int id){
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
