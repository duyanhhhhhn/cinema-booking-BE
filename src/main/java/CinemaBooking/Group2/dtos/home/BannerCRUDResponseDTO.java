package CinemaBooking.Group2.dtos.home;

import CinemaBooking.Group2.models.Banner;

public class BannerCRUDResponseDTO {
	private String message;
	private Banner banner;
	private boolean is_active;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Banner getBanner() {
		return banner;
	}
	public void setBanner(Banner banner) {
		this.banner = banner;
	}
	public boolean isIs_active() {
		return is_active;
	}
	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}
	public BannerCRUDResponseDTO(String message, Banner banner, boolean is_active) {
		super();
		this.message = message;
		this.banner = banner;
		this.is_active = is_active;
	}
	public BannerCRUDResponseDTO() {
		
	}
	public BannerCRUDResponseDTO(String m,boolean is_active) {
		super();
		this.message = m;
		this.is_active=is_active;
	}
}
