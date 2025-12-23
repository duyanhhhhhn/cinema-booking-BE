package CinemaBooking.Group2.dtos.admin;

public class UpdateUserRequestDTO {
	private String fullName;
    private String phone;
    private String avatarUrl;

    /**
     * 1 = ACTIVE
     * 0 = LOCKED
     */
    private Integer isActive;

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
	 * @return the isActive
	 */
	public Integer getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	/**
	 * @param fullName
	 * @param phone
	 * @param avatarUrl
	 * @param isActive
	 */
	public UpdateUserRequestDTO(String fullName, String phone, String avatarUrl, Integer isActive) {
		super();
		this.fullName = fullName;
		this.phone = phone;
		this.avatarUrl = avatarUrl;
		this.isActive = isActive;
	}

	/**
	 * 
	 */
	public UpdateUserRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
