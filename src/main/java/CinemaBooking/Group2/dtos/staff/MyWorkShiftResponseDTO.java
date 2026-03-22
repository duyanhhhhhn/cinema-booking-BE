package CinemaBooking.Group2.dtos.staff;

import java.util.List;

public class MyWorkShiftResponseDTO {
	private int totalShift;
	private int totalHour;
	private int week;
	private List<ShiftResponseDTO> shift;
	public int getTotalShift() {
		return totalShift;
	}
	public void setTotalShift(int totalShift) {
		this.totalShift = totalShift;
	}
	public int getTotalHour() {
		return totalHour;
	}
	public void setTotalHour(int totalHour) {
		this.totalHour = totalHour;
	}
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
	public List<ShiftResponseDTO> getShift() {
		return shift;
	}
	public void setShift(List<ShiftResponseDTO> shift) {
		this.shift = shift;
	}
	public MyWorkShiftResponseDTO() {
		
	}
}
