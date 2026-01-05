package CinemaBooking.Group2.dtos.home;

import java.time.LocalDateTime;

import CinemaBooking.Group2.models.Banner.BannerPosition;

public class BannerResponseDTO {
	private int id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private BannerPosition position;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String message;
    private boolean status;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public BannerResponseDTO(int id, String title, String imageUrl, String linkUrl, BannerPosition position,
			Boolean isActive, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.title = title;
		this.imageUrl = imageUrl;
		this.linkUrl = linkUrl;
		this.position = position;
		this.isActive = isActive;
		this.createdAt = createdAt;
	}
	public BannerResponseDTO() {}
}
