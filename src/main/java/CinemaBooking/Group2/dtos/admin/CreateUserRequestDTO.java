package CinemaBooking.Group2.dtos.admin;

public class CreateUserRequestDTO {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private int roleId;
    private Integer cinemaId;
    private String position;
    private String avatarUrl;
	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	 * @return the roleId
	 */
	public int getRoleId() {
		return roleId;
	}
	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(int roleId) {
		this.roleId = roleId;
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
	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}
	/**
	 * @param fullName
	 * @param email
	 * @param password
	 * @param phone
	 * @param roleId
	 * @param cinemaId
	 * @param position
	 */
	/**
	 * @param fullName
	 * @param email
	 * @param password
	 * @param phone
	 * @param roleId
	 * @param cinemaId
	 * @param position
	 * @param avatarUrl
	 */
	public CreateUserRequestDTO(String fullName, String email, String password, String phone, int roleId,
			Integer cinemaId, String position, String avatarUrl) {
		super();
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.roleId = roleId;
		this.cinemaId = cinemaId;
		this.position = position;
		this.avatarUrl = avatarUrl;
	}
	/**
	 * 
	 */
	public CreateUserRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
