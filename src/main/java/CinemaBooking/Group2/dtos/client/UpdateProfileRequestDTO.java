package CinemaBooking.Group2.dtos.client;

public class UpdateProfileRequestDTO {
	private String fullName;
    private String phone;
    private String email;
	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}
	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
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
	 * @param fullName
	 * @param phone
	 * @param email
	 */
	public UpdateProfileRequestDTO(String fullName, String phone, String email) {
		super();
		this.fullName = fullName;
		this.phone = phone;
		this.email = email;
	}
	/**
	 * 
	 */
	public UpdateProfileRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
    
}
