package CinemaBooking.Group2.dtos.admin;

public class UserResponseDTO {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private int roleId;
    private Integer cinemaId;
    private String position;
    private int isActive;
    private String avatarUrl;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
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
	 * @return the isActive
	 */
	public int getIsActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	/**
	 * @param id
	 * @param fullName
	 * @param email
	 * @param phone
	 * @param roleId
	 * @param cinemaId
	 * @param position
	 * @param isActive
	 * @param avatarUrl
	 */
	public UserResponseDTO(int id, String fullName, String email, String phone, int roleId, Integer cinemaId,
			String position, int isActive, String avatarUrl) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.roleId = roleId;
		this.cinemaId = cinemaId;
		this.position = position;
		this.isActive = isActive;
		this.avatarUrl = avatarUrl;
	}
	/**
	 * 
	 */
	public UserResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}