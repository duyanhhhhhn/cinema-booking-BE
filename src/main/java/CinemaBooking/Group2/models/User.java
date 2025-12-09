package CinemaBooking.Group2.models;

import java.time.LocalDateTime;

public class User {
	public enum UserPosition {
		MANAGER, TICKET_SELLER, TICKET_CHECKER, CLEANER, SECURITY, TECHNICIAN,
	}

	private int id;
	private int roleId;
	private int cinemaId;
	private UserPosition position;
	private String fullName;
	private String email;
	private String password;
	private String phone;
	private String avatarUrl;
	private int isActive;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String roleName;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getCinemaId() {
		return cinemaId;
	}

	public void setCinemaId(int cinemaId) {
		this.cinemaId = cinemaId;
	}

	public UserPosition getPosition() {
		return position;
	}

	public void setPosition(UserPosition position) {
		this.position = position;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * @param id
	 * @param roleId
	 * @param cinemaId
	 * @param position
	 * @param fullName
	 * @param email
	 * @param password
	 * @param phone
	 * @param avatarUrl
	 * @param isActive
	 * @param createdAt
	 * @param updatedAt
	 */
	public User(int id, int roleId, int cinemaId, UserPosition position, String fullName, String email, String password,
			String phone, String avatarUrl, int isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.roleId = roleId;
		this.cinemaId = cinemaId;
		this.position = position;
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.avatarUrl = avatarUrl;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	/**
	 * 
	 */
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

}
