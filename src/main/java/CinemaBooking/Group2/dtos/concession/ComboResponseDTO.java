package CinemaBooking.Group2.dtos.concession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import CinemaBooking.Group2.models.ComboItem;

public class ComboResponseDTO {

	private String message;
	private boolean success;
	private int id;
    private String name;
    private String description;  // text, có thể null
    private BigDecimal price;
    private String imageUrl;
    private int stock;
    private int isActive;
    private LocalDateTime createdAt;
    private List<ComboItem> comboItems;
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
	public List<ComboItem> getComboItems() {
		return comboItems;
	}
	public void setComboItems(List<ComboItem> comboItems) {
		this.comboItems = comboItems;
	}
	public ComboResponseDTO(int id, String name, String description, BigDecimal price, String imageUrl, int stock, int isActive,
			LocalDateTime createdAt, List<ComboItem> comboItems) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imageUrl = imageUrl;
		this.stock = stock;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.comboItems = comboItems;
	}
	public ComboResponseDTO() {
		super();
	}
	public ComboResponseDTO(String message,boolean success) {
		this.message = message;
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
