package CinemaBooking.Group2.dtos.concession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CnPResponseDTO {
	private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private int stock;
    private Integer isActive; // Changed to Integer to allow null for client view
    private LocalDateTime createdAt;
    private String type;
    private List<ComboItemResponseDTO> itemList;
	
	public List<ComboItemResponseDTO> getItemList() {
		return itemList;
	}
	public void setItemList(List<ComboItemResponseDTO> itemList) {
		this.itemList = itemList;
	}
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
	public Integer getIsActive() {
		return isActive;
	}
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public CnPResponseDTO(int id, String name, String description, BigDecimal price, String imageUrl, int stock,
			Integer isActive, LocalDateTime createdAt, String type) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imageUrl = imageUrl;
		this.stock = stock;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.type = type;
	}
	public CnPResponseDTO() {
		
	}
}