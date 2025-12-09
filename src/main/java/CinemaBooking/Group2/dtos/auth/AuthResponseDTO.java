package CinemaBooking.Group2.dtos.auth;

public class AuthResponseDTO {
	private String accessToken;
    private String refreshToken;
    private long expiresInSeconds;
    public AuthResponseDTO(String accessToken, String refreshToken, long expiresInSeconds){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresInSeconds = expiresInSeconds;
    }
	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}
	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	/**
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	/**
	 * @return the expiresInSeconds
	 */
	public long getExpiresInSeconds() {
		return expiresInSeconds;
	}
	/**
	 * @param expiresInSeconds the expiresInSeconds to set
	 */
	public void setExpiresInSeconds(long expiresInSeconds) {
		this.expiresInSeconds = expiresInSeconds;
	}
    
    
}
