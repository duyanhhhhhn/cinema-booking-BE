package CinemaBooking.Group2.dtos.home;

import CinemaBooking.Group2.models.Banner.BannerPosition;

public class BannerRequestDTO {
    private String title;
    private String imageUrl;
    private String linkUrl;
    private BannerPosition position;
    private Boolean isActive;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public BannerRequestDTO(String title, String imageUrl, String linkUrl, BannerPosition position, Boolean isActive) {
		super();
		this.title = title;
		this.imageUrl = imageUrl;
		this.linkUrl = linkUrl;
		this.position = position;
		this.isActive = isActive;
	}
	public BannerRequestDTO() {
		super();
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public BannerPosition getPosition() {
		return position;
	}
	public void setPosition(BannerPosition position) {
		this.position = position;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
}
