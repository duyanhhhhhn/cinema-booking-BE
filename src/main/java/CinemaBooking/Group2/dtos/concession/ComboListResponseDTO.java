package CinemaBooking.Group2.dtos.concession;

import java.util.List;

import CinemaBooking.Group2.models.Combo;

public class ComboListResponseDTO {
	private List<Combo> combo;
	private String message;
	private boolean isSuccess;
	public ComboListResponseDTO() {
		
	}
	public ComboListResponseDTO(List<Combo> combo, String message, boolean isSuccess) {
		super();
		this.combo = combo;
		this.message = message;
		this.isSuccess = isSuccess;
	}
	public List<Combo> getCombo() {
		return combo;
	}
	public void setCombo(List<Combo> combo) {
		this.combo = combo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
}
