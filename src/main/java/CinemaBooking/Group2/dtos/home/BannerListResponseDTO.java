package CinemaBooking.Group2.dtos.home;

import java.util.List;

import CinemaBooking.Group2.models.Banner;

public class BannerListResponseDTO {
	private List<Banner> data;
	private String message;
	public List<Banner> getData() {
		return data;
	}
	public void setData(List<Banner> data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public BannerListResponseDTO(List<Banner> data, String message) {
		super();
		this.data = data;
		this.message = message;
	}
	public BannerListResponseDTO() {
		
	}
}
