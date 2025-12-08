package CinemaBooking.Group2.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking {

	private int id;
    private String bookingCode;
    private int userId;
    private int createdByStaffId;
    private int showtimeId;
    private int voucherId;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public enum PaymentMethod {
        CASH,
        MOMO,
        ZALOPAY,
        VNPAY,
        STRIPE
    }
    
    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBookingCode() {
		return bookingCode;
	}

	public void setBookingCode(String bookingCode) {
		this.bookingCode = bookingCode;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCreatedByStaffId() {
		return createdByStaffId;
	}

	public void setCreatedByStaffId(int createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}

	public int getShowtimeId() {
		return showtimeId;
	}

	public void setShowtimeId(int showtimeId) {
		this.showtimeId = showtimeId;
	}

	public int getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(int voucherId) {
		this.voucherId = voucherId;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}

	/**
	 * @param id
	 * @param bookingCode
	 * @param userId
	 * @param createdByStaffId
	 * @param showtimeId
	 * @param voucherId
	 * @param discountAmount
	 * @param totalPrice
	 * @param paymentStatus
	 * @param paymentMethod
	 * @param createdAt
	 * @param paidAt
	 */
	public Booking(int id, String bookingCode, int userId, int createdByStaffId, int showtimeId, int voucherId,
			BigDecimal discountAmount, BigDecimal totalPrice, PaymentStatus paymentStatus, PaymentMethod paymentMethod,
			LocalDateTime createdAt, LocalDateTime paidAt) {
		super();
		this.id = id;
		this.bookingCode = bookingCode;
		this.userId = userId;
		this.createdByStaffId = createdByStaffId;
		this.showtimeId = showtimeId;
		this.voucherId = voucherId;
		this.discountAmount = discountAmount;
		this.totalPrice = totalPrice;
		this.paymentStatus = paymentStatus;
		this.paymentMethod = paymentMethod;
		this.createdAt = createdAt;
		this.paidAt = paidAt;
	}

	/**
	 * 
	 */
	public Booking() {
		super();
		// TODO Auto-generated constructor stub
	}

	
    
    
}
