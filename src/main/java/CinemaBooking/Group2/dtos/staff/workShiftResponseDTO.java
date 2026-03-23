package CinemaBooking.Group2.dtos.staff;

import java.time.LocalTime;

public class workShiftResponseDTO {
		private int id;
	    private String name;
	    private LocalTime startTime;
	    private LocalTime endTime;
		public workShiftResponseDTO(int id, String name, LocalTime startTime, LocalTime endTime) {
			super();
			this.id = id;
			this.name = name;
			this.startTime = startTime;
			this.endTime = endTime;
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
		/**
		 * @param id
		 * @param name
		 * @param startTime
		 * @param endTime
		 * @param createdAt
		 */
	    

}
