package CinemaBooking.Group2.models;

import java.time.LocalDateTime;

public class Room {

	private int id;
    private int cinemaId;
    private String name;
    private String type;
    private int totalSeats;
    private String seatLayout;
    private LocalDateTime createdAt;
    private Integer status;
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCinemaId() {
		return cinemaId;
	}
	public void setCinemaId(int cinemaId) {
		this.cinemaId = cinemaId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getTotalSeats() {
		return totalSeats;
	}
	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}
	public String getSeatLayout() {
		return seatLayout;
	}
	public void setSeatLayout(String seatLayout) {
		this.seatLayout = seatLayout;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @param id
	 * @param cinemaId
	 * @param name
	 * @param type
	 * @param totalSeats
	 * @param seatLayout
	 * @param createdAt
	 * @param status
	 */
	public Room(int id, int cinemaId, String name, String type, int totalSeats, String seatLayout,
			LocalDateTime createdAt, Integer status) {
		super();
		this.id = id;
		this.cinemaId = cinemaId;
		this.name = name;
		this.type = type;
		this.totalSeats = totalSeats;
		this.seatLayout = seatLayout;
		this.createdAt = createdAt;
		this.status = status;
	}
	/**
	 * 
	 */
	public Room() {
		super();
		// TODO Auto-generated constructor stub
	}

	

}
