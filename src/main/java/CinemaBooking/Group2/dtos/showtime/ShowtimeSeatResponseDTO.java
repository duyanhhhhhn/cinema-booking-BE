package CinemaBooking.Group2.dtos.showtime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import CinemaBooking.Group2.dtos.seat.SeatRowDTO;

public class ShowtimeSeatResponseDTO {
	private int showtimeId;
	private String cinemaName;
	@JsonProperty("seatMap")
    private List<SeatRowDTO> rows;
	/**
	 * @return the showtimeId
	 */
	public int getShowtimeId() {
		return showtimeId;
	}
	/**
	 * @param showtimeId the showtimeId to set
	 */
	public void setShowtimeId(int showtimeId) {
		this.showtimeId = showtimeId;
	}
	/**
	 * @return the cinemaName
	 */
	public String getCinemaName() {
		return cinemaName;
	}
	/**
	 * @param cinemaName the cinemaName to set
	 */
	public void setCinemaName(String cinemaName) {
		this.cinemaName = cinemaName;
	}
	/**
	 * @return the rows
	 */
	public List<SeatRowDTO> getRows() {
		return rows;
	}
	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<SeatRowDTO> rows) {
		this.rows = rows;
	}
	/**
	 * @param showtimeId
	 * @param cinemaName
	 * @param rows
	 */
	public ShowtimeSeatResponseDTO(int showtimeId, String cinemaName, List<SeatRowDTO> rows) {
		super();
		this.showtimeId = showtimeId;
		this.cinemaName = cinemaName;
		this.rows = rows;
	}
	/**
	 * 
	 */
	public ShowtimeSeatResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
    

}
