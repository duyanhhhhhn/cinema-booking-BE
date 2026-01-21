package CinemaBooking.Group2.dtos.dashboardnreports;

import java.util.List;

import CinemaBooking.Group2.models.AuditLog;

public class AuditLogListResponseDTO {
	private List<AuditLog> log;
	private String message;
	private boolean isSuccess;
	public List<AuditLog> getLog() {
		return log;
	}
	public void setLog(List<AuditLog> log) {
		this.log = log;
	}
	public String getMessage() {
		return message;
	}
	public AuditLogListResponseDTO(List<AuditLog> log, String message, boolean isSuccess) {
		super();
		this.log = log;
		this.message = message;
		this.isSuccess = isSuccess;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public AuditLogListResponseDTO() {
		super();
	}
}
