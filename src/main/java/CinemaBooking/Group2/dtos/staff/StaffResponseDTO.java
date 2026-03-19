package CinemaBooking.Group2.dtos.staff;

import CinemaBooking.Group2.models.User.UserPosition;

public class StaffResponseDTO {
	private String avatarUrl;
	private String fullName;
	private String phone;
	private String avatar;
	private UserPosition position;
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public UserPosition getPosition() {
		return position;
	}
	public void setPosition(UserPosition position) {
		this.position = position;
	}
	public StaffResponseDTO() {
		
	}
}
