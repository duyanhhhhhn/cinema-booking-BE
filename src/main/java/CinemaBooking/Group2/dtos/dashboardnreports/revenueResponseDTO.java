package CinemaBooking.Group2.dtos.dashboardnreports;

import java.math.BigDecimal;
import java.time.LocalDate;

import CinemaBooking.Group2.models.Enum.Status;

public class revenueResponseDTO {
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	private int id;
	private BigDecimal revenue;
	private LocalDate date;
	private String message;
	private Status status;
	public BigDecimal getRevenue() {
		return revenue;
	}
	public void setRevenue(BigDecimal revenue) {
		this.revenue = revenue;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public revenueResponseDTO(BigDecimal revenue, String message, Status status) {
		super();
		this.revenue = revenue;
		this.message = message;
		this.status = status;
	}
	public revenueResponseDTO() {}
}
