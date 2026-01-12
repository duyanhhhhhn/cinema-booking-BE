package CinemaBooking.Group2.dtos.staff;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class workShiftResponseDTO {
		private int id;
	    private String name;
	    private LocalTime startTime;
	    private LocalTime endTime;
	    private LocalDateTime createdAt;
		public workShiftResponseDTO(int id, String name, LocalTime startTime, LocalTime endTime,
				LocalDateTime createdAt) {
			super();
			this.id = id;
			this.name = name;
			this.startTime = startTime;
			this.endTime = endTime;
			this.createdAt = createdAt;
		}
		public workShiftResponseDTO () {}
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
	    

}
