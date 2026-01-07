package CinemaBooking.Group2.dtos.dashboardnreports;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import CinemaBooking.Group2.models.Enum.PaymentMethod;
import CinemaBooking.Group2.models.Enum.PaymentStatus;

public class paymentResponseDTO {
	private int id;
    private int bookingId;
    private BigDecimal amount;
    private PaymentMethod method;
    private String providerPaymentId;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
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
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public PaymentMethod getMethod() {
		return method;
	}
	public void setMethod(PaymentMethod method) {
		this.method = method;
	}
	public String getProviderPaymentId() {
		return providerPaymentId;
	}
	public void setProviderPaymentId(String providerPaymentId) {
		this.providerPaymentId = providerPaymentId;
	}
	public PaymentStatus getStatus() {
		return status;
	}
	public void setStatus(PaymentStatus status) {
		this.status = status;
	}
	public LocalDateTime getPaidAt() {
		return paidAt;
	}
	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * @param id
	 * @param bookingId
	 * @param amount
	 * @param method
	 * @param providerPaymentId
	 * @param status
	 * @param paidAt
	 * @param createdAt
	 */
	public paymentResponseDTO(int id, int bookingId, BigDecimal amount, PaymentMethod method, String providerPaymentId,
			PaymentStatus status, LocalDateTime paidAt, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.bookingId = bookingId;
		this.amount = amount;
		this.method = method;
		this.providerPaymentId = providerPaymentId;
		this.status = status;
		this.paidAt = paidAt;
		this.createdAt = createdAt;
	}
	/**
	 * 
	 */
	public paymentResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}
