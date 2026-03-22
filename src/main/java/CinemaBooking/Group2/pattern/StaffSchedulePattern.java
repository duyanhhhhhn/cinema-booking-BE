package CinemaBooking.Group2.pattern;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.WorkShift;
import CinemaBooking.Group2.repositories.ScheduleRepository;
import CinemaBooking.Group2.repositories.ShiftRepository;
import CinemaBooking.Group2.repositories.UserRepository;

@Repository
public class StaffSchedulePattern {
	@Autowired
	private ScheduleRepository scheRep;
	@Autowired
	public ShiftRepository shiftRep;
	@Autowired
	public UserRepository userRep;
	public StaffSchedulePattern() {
	}
	//Staff Schedule
	public List<StaffSchedule> getSchedule(int page,int size){
		try {
			return scheRep.getAll(page,size);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	};
	public StaffSchedule getScheduleByDate(LocalDate date){
		try {
			return scheRep.getSchedulesByDate(date);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public List<StaffSchedule> getScheduleByRange(LocalDate startDate,LocalDate endDate) {
		try {
			return scheRep.getShedulesByRange(startDate, endDate);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public List<StaffSchedule> getScheduleByStaffId(int staff_id,LocalDate startDate,LocalDate endDate){
		try {
			return scheRep.getByStaffId(staff_id,startDate,endDate);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	};
	public List<StaffSchedule> getScheduleByShiftId(int shift_id){
		try {
			return scheRep.getByShift(shift_id);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public int assignStaff(StaffSchedule item) {
		try {
			return scheRep.assignSchedule(item);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	//Work Shift
	public List<WorkShift> getShift(){
		try {
			return shiftRep.getAll();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public WorkShift findByShiftId(int id) {
		try {
			return shiftRep.findById(id);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public List<WorkShift> findByStaffId(int id, LocalDate date){
		try {
			return shiftRep.findByStaffId(id);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	//user
	public User findStaffById(int id) {
		try {
			return scheRep.findStaffById(id);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public List<User> getAllStaff(){
		try {
			return scheRep.getAllStaff();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public WorkShift getWorkShiftByDate(LocalDate date) {
		try {
			return shiftRep.getByDate(date);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
