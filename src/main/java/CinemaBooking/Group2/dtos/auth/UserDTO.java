package CinemaBooking.Group2.dtos.auth;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;

public class UserDTO {

	@Value("${app.upload.public-prefix:/media}")
	private String publicPrefix;

	private int id;
	private String fullName;
	private String email;
	private String phone;
	private String avatarUrl;
	private String role;
	private String position;
	private LocalDateTime createdAt;

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

	private Integer cinemaId;
	private int isActive;

	// ================= CONSTRUCTORS =================

	/** Default constructor */
	public UserDTO() {
	}

	/** Constructor without createdAt and isActive */
	public UserDTO(int id, String fullName, String email, String phone, String avatarUrl, String role,
			Integer cinemaId) {
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.avatarUrl = avatarUrl;
		this.role = role;
		this.cinemaId = cinemaId;
	}

	/**
	 * @param publicPrefix
	 * @param id
	 * @param fullName
	 * @param email
	 * @param phone
	 * @param avatarUrl
	 * @param role
	 * @param createdAt
	 * @param cinemaId
	 * @param position
	 * @param isActive
	 */
	public UserDTO(String publicPrefix, int id, String fullName, String email, String phone, String avatarUrl,
			String role, LocalDateTime createdAt, Integer cinemaId, String position, int isActive) {
		super();
		this.publicPrefix = publicPrefix;
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.avatarUrl = avatarUrl;
		this.role = role;
		this.createdAt = createdAt;
		this.cinemaId = cinemaId;
		this.position = position;
		this.isActive = isActive;
	}

	/** Full constructor without publicPrefix (fix lỗi bạn đang gặp) */
	public UserDTO(int id, String fullName, String email, String phone, String avatarUrl, String role,
			LocalDateTime createdAt, Integer cinemaId, int isActive) {
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.avatarUrl = avatarUrl;
		this.role = role;
		this.createdAt = createdAt;
		this.cinemaId = cinemaId;
		this.isActive = isActive;
	}

	// ================= GETTERS & SETTERS =================

	public String getPublicPrefix() {
		return publicPrefix;
	}

	public void setPublicPrefix(String publicPrefix) {
		this.publicPrefix = publicPrefix;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAvatarUrl() {
		if (avatarUrl == null || avatarUrl.isBlank())
			return null;

		String prefix = publicPrefix != null ? publicPrefix : "/media";
		if (!prefix.startsWith("/"))
			prefix = "/" + prefix;
		while (prefix.endsWith("/"))
			prefix = prefix.substring(0, prefix.length() - 1);

		String path = avatarUrl;
		if (path.startsWith("/"))
			path = path.substring(1);

		return prefix + "/" + path;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getCinemaId() {
		return cinemaId;
	}

	public void setCinemaId(Integer cinemaId) {
		this.cinemaId = cinemaId;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public UserDTO(int id, String fullName, String email, String phone, String avatarUrl, String role,
			LocalDateTime createdAt, Integer cinemaId, String position, int isActive) {
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.avatarUrl = avatarUrl;
		this.role = role;
		this.createdAt = createdAt;
		this.cinemaId = cinemaId;
		this.position = position;
		this.isActive = isActive;
	}
}
