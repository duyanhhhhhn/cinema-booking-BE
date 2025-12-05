package CinemaBooking.Group2.models;

import java.time.LocalDateTime;

public class RefreshToken {

	private int id;
    private int userId;
    private String token;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private int revoked;
    private LocalDateTime revokedAt;
    private String ipAddress;
    private String userAgent;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public LocalDateTime getIssuedAt() {
		return issuedAt;
	}
	public void setIssuedAt(LocalDateTime issuedAt) {
		this.issuedAt = issuedAt;
	}
	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
	public int getRevoked() {
		return revoked;
	}
	public void setRevoked(int revoked) {
		this.revoked = revoked;
	}
	public LocalDateTime getRevokedAt() {
		return revokedAt;
	}
	public void setRevokedAt(LocalDateTime revokedAt) {
		this.revokedAt = revokedAt;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	/**
	 * @param id
	 * @param userId
	 * @param token
	 * @param issuedAt
	 * @param expiresAt
	 * @param revoked
	 * @param revokedAt
	 * @param ipAddress
	 * @param userAgent
	 */
	public RefreshToken(int id, int userId, String token, LocalDateTime issuedAt, LocalDateTime expiresAt, int revoked,
			LocalDateTime revokedAt, String ipAddress, String userAgent) {
		super();
		this.id = id;
		this.userId = userId;
		this.token = token;
		this.issuedAt = issuedAt;
		this.expiresAt = expiresAt;
		this.revoked = revoked;
		this.revokedAt = revokedAt;
		this.ipAddress = ipAddress;
		this.userAgent = userAgent;
	}
	/**
	 * 
	 */
	public RefreshToken() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
