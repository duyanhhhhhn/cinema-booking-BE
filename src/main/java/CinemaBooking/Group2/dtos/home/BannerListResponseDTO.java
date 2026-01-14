package CinemaBooking.Group2.dtos.home;

import java.util.List;

import CinemaBooking.Group2.models.Banner;

public class BannerListResponseDTO {
	private List<Banner> banners;
	private boolean is_success;
	private String message;
	public List<Banner> getBanners() {
		return banners;
	}
	public void setBanners(List<Banner> banners) {
		this.banners = banners;
	}
	public boolean isIs_success() {
		return is_success;
	}
	public void setIs_success(boolean is_success) {
		this.is_success = is_success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public BannerListResponseDTO(List<Banner> banners, boolean is_success, String message) {
		super();
		this.banners = banners;
		this.is_success = is_success;
		this.message = message;
	}
	public BannerListResponseDTO() {
		
	}
}
