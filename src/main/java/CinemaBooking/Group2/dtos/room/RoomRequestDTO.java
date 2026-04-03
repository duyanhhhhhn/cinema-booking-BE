package CinemaBooking.Group2.dtos.room;

public class RoomRequestDTO {
    private Integer cinemaId;
    private String name;
    private String type;
    private Integer totalSeats;
    private String seatLayout;
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
	/**
	 * @return the cinemaId
	 */
	public Integer getCinemaId() {
		return cinemaId;
	}
	/**
	 * @param cinemaId the cinemaId to set
	 */
	public void setCinemaId(Integer cinemaId) {
		this.cinemaId = cinemaId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the totalSeats
	 */
	public Integer getTotalSeats() {
		return totalSeats;
	}
	/**
	 * @param totalSeats the totalSeats to set
	 */
	public void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}
	/**
	 * @return the seatLayout
	 */
	public String getSeatLayout() {
		return seatLayout;
	}
	/**
	 * @param seatLayout the seatLayout to set
	 */
	public void setSeatLayout(String seatLayout) {
		this.seatLayout = seatLayout;
	}
	/**
	 * @param cinemaId
	 * @param name
	 * @param type
	 * @param totalSeats
	 * @param seatLayout
	 * @param status
	 */
	public RoomRequestDTO(Integer cinemaId, String name, String type, Integer totalSeats, String seatLayout,
			Integer status) {
		super();
		this.cinemaId = cinemaId;
		this.name = name;
		this.type = type;
		this.totalSeats = totalSeats;
		this.seatLayout = seatLayout;
		this.status = status;
	}
	/**
	 * 
	 */
	public RoomRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}