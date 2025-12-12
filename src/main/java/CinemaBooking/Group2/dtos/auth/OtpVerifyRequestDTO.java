package CinemaBooking.Group2.dtos.auth;

public class OtpVerifyRequestDTO {
    private String email;
    private String otpCode;
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the otpCode
	 */
	public String getOtpCode() {
		return otpCode;
	}
	/**
	 * @param otpCode the otpCode to set
	 */
	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}
    
	
}
