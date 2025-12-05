package CinemaBooking.Group2.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Combo {

	private int id;
    private String name;
    private String description;  // text, có thể null
    private BigDecimal price;
    private String imageUrl;
    private int isActive;
    private LocalDateTime createdAt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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
	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param price
	 * @param imageUrl
	 * @param isActive
	 * @param createdAt
	 */
	public Combo(int id, String name, String description, BigDecimal price, String imageUrl, int isActive,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imageUrl = imageUrl;
		this.isActive = isActive;
		this.createdAt = createdAt;
	}
	/**
	 * 
	 */
	public Combo() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
