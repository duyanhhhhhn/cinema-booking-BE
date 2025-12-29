package CinemaBooking.Group2.pattern;

import java.util.List;

import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.WorkShift;
import CinemaBooking.Group2.repositories.ScheduleRepository;
import CinemaBooking.Group2.repositories.ShiftRepository;

public class StaffSchedulePattern {
	private static StaffSchedulePattern _instance=null;
	private ScheduleRepository scheRep;
	private ShiftRepository shiftRep;
	private StaffSchedulePattern() {
		scheRep = ScheduleRepository.Instance();
		shiftRep = ShiftRepository.Instance();
	}
	public static StaffSchedulePattern Instance() {
		if(_instance==null) {
			_instance=new StaffSchedulePattern();
		}
		return _instance;
	}
	//Staff Schedule
	public List<StaffSchedule> getSchedule(){
		try {
			return scheRep.getAll();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	};
	public List<StaffSchedule> getScheduleByStaffId(int staff_id){
		try {
			return scheRep.getByStaffId(staff_id);
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
}
