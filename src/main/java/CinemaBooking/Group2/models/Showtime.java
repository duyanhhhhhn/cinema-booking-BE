package CinemaBooking.Group2.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Showtime {

	public enum ShowtimeStatus {
	    SCHEDULED,
	    CANCELLED,
	    COMPLETED
	}
	
	private int id;
    private int movieId;
    private int roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal basePrice;
    private ShowtimeStatus status;
    private LocalDateTime createdAt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	public BigDecimal getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}
	public ShowtimeStatus getStatus() {
		return status;
	}
	public void setStatus(ShowtimeStatus status) {
		this.status = status;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @param id
	 * @param movieId
	 * @param roomId
	 * @param startTime
	 * @param endTime
	 * @param basePrice
	 * @param status
	 * @param createdAt
	 */
	public Showtime(int id, int movieId, int roomId, LocalDateTime startTime, LocalDateTime endTime,
			BigDecimal basePrice, ShowtimeStatus status, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.movieId = movieId;
		this.roomId = roomId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.basePrice = basePrice;
		this.status = status;
		this.createdAt = createdAt;
	}
	/**
	 * 
	 */
	public Showtime() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
