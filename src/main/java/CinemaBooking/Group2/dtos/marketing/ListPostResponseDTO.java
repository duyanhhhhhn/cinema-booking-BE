package CinemaBooking.Group2.dtos.marketing;

import java.util.List;

import CinemaBooking.Group2.models.Post;

public class ListPostResponseDTO {
	private List<Post> list;
	private boolean is_success;
	private String message;
	public List<Post> getList() {
		return list;
	}
	public void setList(List<Post> list) {
		this.list = list;
	}
	public boolean isIs_success() {
		return is_success;
	}
	public void setIs_success(boolean is_success) {
		this.is_success = is_success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ListPostResponseDTO() {
		
	}
	public ListPostResponseDTO(List<Post> list, boolean is_success, String message) {
		super();
		this.list = list;
		this.is_success = is_success;
		this.message = message;
	}
}
