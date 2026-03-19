package CinemaBooking.Group2.dtos.staff;

import java.util.List;

import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;

public class AWeekOfScheduleResponseDTO {
		private int id;
		private StaffResponseDTO staff;
		private List<ShiftResponseDTO> shift;
		private StaffScheduleStatus status;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public StaffResponseDTO getStaff() {
			return staff;
		}
		public List<ShiftResponseDTO> getShift() {
			return shift;
		}
		public void setShift(List<ShiftResponseDTO> shift) {
			this.shift = shift;
		}
		public void setStaff(StaffResponseDTO staff) {
			this.staff = staff;
		}
		public StaffScheduleStatus getStatus() {
			return status;
		}
		public void setStatus(StaffScheduleStatus status) {
			this.status = status;
		}

}
