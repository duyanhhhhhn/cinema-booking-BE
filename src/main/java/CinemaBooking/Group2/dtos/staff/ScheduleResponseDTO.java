package CinemaBooking.Group2.dtos.staff;

import java.time.LocalDate;

import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.WorkShift;
import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;

public class ScheduleResponseDTO {
	private int id;
	private User staff;
	private WorkShift shift;
	private LocalDate workdate;
	private StaffScheduleStatus status;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getStaff() {
		return staff;
	}
	public void setStaff(User staff) {
		this.staff = staff;
	}
	public WorkShift getShift() {
		return shift;
	}
	public void setShift(WorkShift shift) {
		this.shift = shift;
	}
	public LocalDate getWorkdate() {
		return workdate;
	}
	public void setWorkdate(LocalDate workdate) {
		this.workdate = workdate;
	}
	public StaffScheduleStatus getStatus() {
		return status;
	}
	public void setStatus(StaffScheduleStatus status) {
		this.status = status;
	}
}
