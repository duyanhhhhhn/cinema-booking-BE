package CinemaBooking.Group2.models;

import java.time.LocalDateTime;

public class Banner {

	private int id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private BannerPosition position;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    public enum BannerPosition {
        HOME,
        MOVIE_DETAIL,
        PROMO
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

	/**
	 * @param id
	 * @param title
	 * @param imageUrl
	 * @param linkUrl
	 * @param position
	 * @param isActive
	 * @param createdAt
	 */
	public Banner(int id, String title, String imageUrl, String linkUrl, BannerPosition position, Boolean isActive,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.title = title;
		this.imageUrl = imageUrl;
		this.linkUrl = linkUrl;
		this.position = position;
		this.isActive = isActive;
		this.createdAt = createdAt;
	}

	/**
	 * 
	 */
	public Banner() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
