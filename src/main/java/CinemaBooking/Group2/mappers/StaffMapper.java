package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.dtos.staff.staffScheduleResponseDTO;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;

public class StaffMapper implements RowMapper<StaffSchedule>{
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
