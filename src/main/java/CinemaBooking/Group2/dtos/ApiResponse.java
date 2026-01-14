package CinemaBooking.Group2.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonPropertyOrder({ "message", "data", "meta" })
public class ApiResponse<T> {
	private String message;
    private T data;
    private Object meta;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message, T data, Object meta) {
        this.message = message;
        this.data = data;
        this.meta = meta;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public Object getMeta() { return meta; }
    public void setMeta(Object meta) { this.meta = meta; }
}
