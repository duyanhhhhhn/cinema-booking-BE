package CinemaBooking.Group2.dtos.staff;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;

public class ShiftResponseDTO {
	private int id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private LocalDate WorkDate;
    private StaffScheduleStatus status;
	public StaffScheduleStatus getStatus() {
		return status;
	}
	public void setStatus(StaffScheduleStatus status) {
		this.status = status;
	}
	public LocalDate getWorkDate() {
		return WorkDate;
	}
	public void setWorkDate(LocalDate workDate) {
		WorkDate = workDate;
	}
	public ShiftResponseDTO(int id, String name, LocalTime startTime, LocalTime endTime,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.createdAt = createdAt;
	}
	public ShiftResponseDTO () {}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}
	public LocalTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
