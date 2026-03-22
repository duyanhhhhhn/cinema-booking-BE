package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.staff.ScheduleResponseDTO;
import CinemaBooking.Group2.dtos.staff.StaffResponseDTO;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;
import CinemaBooking.Group2.pattern.StaffSchedulePattern;

public class StaffMapper implements RowMapper<StaffSchedule>{
	@Autowired
	private static StaffSchedulePattern schedule;
	public static ScheduleResponseDTO toResponseDTO(StaffSchedule item) {
		if(item==null) {
			return null;
		}
		ScheduleResponseDTO staff = new ScheduleResponseDTO();
		staff.setId(item.getId());
		staff.setStatus(item.getStatus());
		User user = schedule.findStaffById(item.getStaffId());
		StaffResponseDTO staffDTO = new StaffResponseDTO();
		staffDTO.setFullName(user.getFullName());
		staffDTO.setAvatarUrl(user.getAvatarUrl());
		staffDTO.setPhone(user.getPhone());
		staff.setStaff(staffDTO);
		staff.setShift(schedule.findByShiftId(item.getShiftId()));
		staff.setWorkdate(item.getWorkDate());
		return staff;
	}
	@Override
	public StaffSchedule mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		StaffSchedule item =  new StaffSchedule();
		item.setId(rs.getInt("id"));
		item.setShiftId(rs.getInt("shift_id"));
		item.setStaffId(rs.getInt("staff_id"));
		item.setWorkDate(rs.getDate("work_date").toLocalDate());
		item.setStatus(StaffScheduleStatus.valueOf(rs.getString("status")));
		
		return item;
	}
}
