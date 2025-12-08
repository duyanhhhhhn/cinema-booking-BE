package CinemaBooking.Group2.models;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class WorkShift {
	private int id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
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
	/**
	 * @param id
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @param createdAt
	 */
	public WorkShift(int id, String name, LocalTime startTime, LocalTime endTime, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.createdAt = createdAt;
	}
	/**
	 * 
	 */
	public WorkShift() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
