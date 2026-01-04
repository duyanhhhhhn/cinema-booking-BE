package CinemaBooking.Group2.dtos;

public class ApiResponse<T> {
	private String message;
    private T data;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public ApiResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ApiResponse(String message, T data) {
		this.message = message;
		this.data = data;
	}
}
