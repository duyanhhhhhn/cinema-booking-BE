package CinemaBooking.Group2.models;

import java.time.LocalDateTime;

public class SeatHold {

	private int id;
    private int showtimeId;
    private int seatId;
    private int userId;
    private String holdToken;
    private LocalDateTime holdExpiresAt;
    private LocalDateTime createdAt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getShowtimeId() {
		return showtimeId;
	}
	public void setShowtimeId(int showtimeId) {
		this.showtimeId = showtimeId;
	}
	public int getSeatId() {
		return seatId;
	}
	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getHoldToken() {
		return holdToken;
	}
	public void setHoldToken(String holdToken) {
		this.holdToken = holdToken;
	}
	public LocalDateTime getHoldExpiresAt() {
		return holdExpiresAt;
	}
	public void setHoldExpiresAt(LocalDateTime holdExpiresAt) {
		this.holdExpiresAt = holdExpiresAt;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @param id
	 * @param showtimeId
	 * @param seatId
	 * @param userId
	 * @param holdToken
	 * @param holdExpiresAt
	 * @param createdAt
	 */
	public SeatHold(int id, int showtimeId, int seatId, int userId, String holdToken, LocalDateTime holdExpiresAt,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.showtimeId = showtimeId;
		this.seatId = seatId;
		this.userId = userId;
		this.holdToken = holdToken;
		this.holdExpiresAt = holdExpiresAt;
		this.createdAt = createdAt;
	}
	/**
	 * 
	 */
	public SeatHold() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
