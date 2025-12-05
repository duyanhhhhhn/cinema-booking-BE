package CinemaBooking.Group2.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingSeat {

	private int id;
    private int bookingId;
    private int seatId;
    private BigDecimal seatPrice;
    private String ticketCode;
    private Boolean isPrinted;
    private int printedBy;
    private LocalDateTime printedAt;
    private String printedStamp;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBookingId() {
		return bookingId;
	}
	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}
	public int getSeatId() {
		return seatId;
	}
	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}
	public BigDecimal getSeatPrice() {
		return seatPrice;
	}
	public void setSeatPrice(BigDecimal seatPrice) {
		this.seatPrice = seatPrice;
	}
	public String getTicketCode() {
		return ticketCode;
	}
	public void setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
	}
	public Boolean getIsPrinted() {
		return isPrinted;
	}
	public void setIsPrinted(Boolean isPrinted) {
		this.isPrinted = isPrinted;
	}
	public int getPrintedBy() {
		return printedBy;
	}
	public void setPrintedBy(int printedBy) {
		this.printedBy = printedBy;
	}
	public LocalDateTime getPrintedAt() {
		return printedAt;
	}
	public void setPrintedAt(LocalDateTime printedAt) {
		this.printedAt = printedAt;
	}
	public String getPrintedStamp() {
		return printedStamp;
	}
	public void setPrintedStamp(String printedStamp) {
		this.printedStamp = printedStamp;
	}
	/**
	 * @param id
	 * @param bookingId
	 * @param seatId
	 * @param seatPrice
	 * @param ticketCode
	 * @param isPrinted
	 * @param printedBy
	 * @param printedAt
	 * @param printedStamp
	 */
	public BookingSeat(int id, int bookingId, int seatId, BigDecimal seatPrice, String ticketCode, Boolean isPrinted,
			int printedBy, LocalDateTime printedAt, String printedStamp) {
		super();
		this.id = id;
		this.bookingId = bookingId;
		this.seatId = seatId;
		this.seatPrice = seatPrice;
		this.ticketCode = ticketCode;
		this.isPrinted = isPrinted;
		this.printedBy = printedBy;
		this.printedAt = printedAt;
		this.printedStamp = printedStamp;
	}
	/**
	 * 
	 */
	public BookingSeat() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
