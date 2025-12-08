package CinemaBooking.Group2.models;

import java.math.BigDecimal;

public class BookingConcession {

	private int id;
    private int bookingId;
    private int productId;   // nếu không dùng thì có thể Null
    private int comboId;     // nếu không dùng thì có thể Null
    private int quantity;
    private BigDecimal price;
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
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getComboId() {
		return comboId;
	}
	public void setComboId(int comboId) {
		this.comboId = comboId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * @param id
	 * @param bookingId
	 * @param productId
	 * @param comboId
	 * @param quantity
	 * @param price
	 */
	public BookingConcession(int id, int bookingId, int productId, int comboId, int quantity, BigDecimal price) {
		super();
		this.id = id;
		this.bookingId = bookingId;
		this.productId = productId;
		this.comboId = comboId;
		this.quantity = quantity;
		this.price = price;
	}
	/**
	 * 
	 */
	public BookingConcession() {
		super();
		// TODO Auto-generated constructor stub
	}

    
}
