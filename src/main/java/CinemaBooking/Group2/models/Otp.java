package CinemaBooking.Group2.models;

import java.time.LocalDateTime;

public class Otp {

	public enum OtpPurpose {
	    REGISTER,
	    FORGOT_PASSWORD,
	    TWO_FACTOR
	}
	
	private int id;
    private int userId;
    /**
	 * 
	 */
	public Otp() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param id
	 * @param userId
	 * @param email
	 * @param code
	 * @param purpose
	 * @param expiresAt
	 * @param used
	 * @param createdAt
	 */
	public Otp(int id, int userId, String email, String code, OtpPurpose purpose, LocalDateTime expiresAt, int used,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.userId = userId;
		this.email = email;
		this.code = code;
		this.purpose = purpose;
		this.expiresAt = expiresAt;
		this.used = used;
		this.createdAt = createdAt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public OtpPurpose getPurpose() {
		return purpose;
	}
	public void setPurpose(OtpPurpose purpose) {
		this.purpose = purpose;
	}
	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
	public int getUsed() {
		return used;
	}
	public void setUsed(int used) {
		this.used = used;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	private String email;
    private String code;
    private OtpPurpose purpose;
    private LocalDateTime expiresAt;
    private int used;
    private LocalDateTime createdAt;
    
    

}
