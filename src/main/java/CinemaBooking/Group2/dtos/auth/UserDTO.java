package CinemaBooking.Group2.dtos.auth;

public class UserDTO {
	private int id;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String role;
    private Integer cinemaId;
	/**
	 * @param id
	 * @param fullName
	 * @param email
	 * @param phone
	 * @param avatarUrl
	 * @param role
	 * @param cinemaId
	 */
	public UserDTO(int id, String fullName, String email, String phone, String avatarUrl, String role,
			Integer cinemaId) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.avatarUrl = avatarUrl;
		this.role = role;
		this.cinemaId = cinemaId;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
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
	 * @return the avatarUrl
	 */
	public String getAvatarUrl() {
		return avatarUrl;
	}
	/**
	 * @param avatarUrl the avatarUrl to set
	 */
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}
	/**
	 * @return the cinemaId
	 */
	public Integer getCinemaId() {
		return cinemaId;
	}
	/**
	 * @param cinemaId the cinemaId to set
	 */
	public void setCinemaId(Integer cinemaId) {
		this.cinemaId = cinemaId;
	}
}
