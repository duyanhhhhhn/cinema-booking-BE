package CinemaBooking.Group2.dtos.dashboardnreports;

import java.math.BigDecimal;

import CinemaBooking.Group2.models.Enum.Status;

public class revenueResponseDTO {
	private BigDecimal revenue;
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
