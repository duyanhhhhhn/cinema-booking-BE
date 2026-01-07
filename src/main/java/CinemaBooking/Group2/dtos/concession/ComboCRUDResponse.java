package CinemaBooking.Group2.dtos.concession;

import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.Enum.ResponseStatus;

public class ComboCRUDResponse {
	private ResponseStatus status;
	private String message;
	private Combo combo;
	public ComboCRUDResponse(ResponseStatus status, String message, Combo combo) {
		super();
		this.status = status;
		this.message = message;
		this.combo = combo;
	}
	public ResponseStatus isStatus() {
		return status;
	}
	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Combo getCombo() {
		return combo;
	}
	public void setCombo(Combo combo) {
		this.combo = combo;
	}
	public ComboCRUDResponse() {
		
	}
}
