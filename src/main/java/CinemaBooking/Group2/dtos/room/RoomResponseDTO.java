package CinemaBooking.Group2.dtos.room;

public class RoomResponseDTO {
	private int id;
    private int cinemaId;
    private String name;
    private String type;
    private int totalSeats;
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
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the cinemaId
	 */
	public int getCinemaId() {
		return cinemaId;
	}
	/**
	 * @param cinemaId the cinemaId to set
	 */
	public void setCinemaId(int cinemaId) {
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
	public int getTotalSeats() {
		return totalSeats;
	}
	/**
	 * @param totalSeats the totalSeats to set
	 */
	public void setTotalSeats(int totalSeats) {
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
	 * @param id
	 * @param cinemaId
	 * @param name
	 * @param type
	 * @param totalSeats
	 * @param seatLayout
	 * @param status
	 */
	public RoomResponseDTO(int id, int cinemaId, String name, String type, int totalSeats, String seatLayout,
			Integer status) {
		super();
		this.id = id;
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
	public RoomResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
