package CinemaBooking.Group2.models;

import java.math.BigDecimal;

public class Seat {

	public enum SeatType {
	    STANDARD,
	    VIP,
	    COUPLE
	}
	
	private int id;
    private int roomId;
    private String seatCode;
    private SeatType seatType;
    private BigDecimal extraPrice;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public String getSeatCode() {
		return seatCode;
	}
	public void setSeatCode(String seatCode) {
		this.seatCode = seatCode;
	}
	public SeatType getSeatType() {
		return seatType;
	}
	public void setSeatType(SeatType seatType) {
		this.seatType = seatType;
	}
	public BigDecimal getExtraPrice() {
		return extraPrice;
	}
	public void setExtraPrice(BigDecimal extraPrice) {
		this.extraPrice = extraPrice;
	}
	/**
	 * @param id
	 * @param roomId
	 * @param seatCode
	 * @param seatType
	 * @param extraPrice
	 */
	public Seat(int id, int roomId, String seatCode, SeatType seatType, BigDecimal extraPrice) {
		super();
		this.id = id;
		this.roomId = roomId;
		this.seatCode = seatCode;
		this.seatType = seatType;
		this.extraPrice = extraPrice;
	}
	/**
	 * 
	 */
	public Seat() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
