package CinemaBooking.Group2.dtos.seat;

import java.util.List;

public class SeatRowDTO {
	private String rowLabel;
    private List<SeatDTO> seats;
	/**
	 * @return the rowLabel
	 */
	public String getRowLabel() {
		return rowLabel;
	}
	/**
	 * @param rowLabel the rowLabel to set
	 */
	public void setRowLabel(String rowLabel) {
		this.rowLabel = rowLabel;
	}
	/**
	 * @return the seats
	 */
	public List<SeatDTO> getSeats() {
		return seats;
	}
	/**
	 * 
	 */
	public SeatRowDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param rowLabel
	 * @param seats
	 */
	public SeatRowDTO(String rowLabel, List<SeatDTO> seats) {
		super();
		this.rowLabel = rowLabel;
		this.seats = seats;
	}
	/**
	 * @param seats the seats to set
	 */
	public void setSeats(List<SeatDTO> seats) {
		this.seats = seats;
	}
}
