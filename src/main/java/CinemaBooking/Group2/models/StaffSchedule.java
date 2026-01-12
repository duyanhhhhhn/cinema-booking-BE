package CinemaBooking.Group2.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;

public class StaffSchedule {
	private int id;
	private int staffId;
	private int shiftId;
	private LocalDate workDate;
	private StaffScheduleStatus status;
	private LocalDateTime createdAt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStaffId() {
		return staffId;
	}
	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}
	public int getShiftId() {
		return shiftId;
	}
	public void setShiftId(int shiftId) {
		this.shiftId = shiftId;
	}
	public LocalDate getWorkDate() {
		return workDate;
	}
	public void setWorkDate(LocalDate workDate) {
		this.workDate = workDate;
	}
	public StaffScheduleStatus getStatus() {
		return status;
	}
	public void setStatus(StaffScheduleStatus status) {
		this.status = status;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @param id
	 * @param staffId
	 * @param shiftId
	 * @param workDate
	 * @param status
	 * @param createdAt
	 */
	public StaffSchedule(int id, int staffId, int shiftId, LocalDate workDate, StaffScheduleStatus status,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.staffId = staffId;
		this.shiftId = shiftId;
		this.workDate = workDate;
		this.status = status;
		this.createdAt = createdAt;
	}
	/**
	 * 
	 */
	public StaffSchedule() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
