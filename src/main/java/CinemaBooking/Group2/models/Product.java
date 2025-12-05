package CinemaBooking.Group2.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {

	private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private int stock;
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
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
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
	 * @param stock
	 * @param isActive
	 * @param createdAt
	 */
	public Product(int id, String name, String description, BigDecimal price, String imageUrl, int stock, int isActive,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imageUrl = imageUrl;
		this.stock = stock;
		this.isActive = isActive;
		this.createdAt = createdAt;
	}
	/**
	 * 
	 */
	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
