package CinemaBooking.Group2.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Combo {

	private int id;
    private String name;
    private String description;  // text, có thể null
    private BigDecimal price;
    private String imageUrl="no_img.jpg";
    private int isActive = 0;
    private LocalDateTime createdAt;
    private List<ComboItem> comboItems = new ArrayList<>();
	public List<ComboItem> getComboItems() {
		return comboItems;
	}
	public void setComboItems(List<ComboItem> comboItems) {
		this.comboItems = comboItems;
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
	public Combo() {
		// TODO Auto-generated constructor stub
		super();
	}
	@Override
	public String toString() {
	    return "Combo{" +
	            "id=" + id +
	            ", name='" + name + '\'' +
	            ", description='" + description + '\'' +
	            ", price=" + price +
	            ", imageUrl='" + imageUrl + '\'' +
	            ", isActive=" + isActive +
	            ", createdAt=" + createdAt +
	            ", comboItems=" + comboItems +
	            '}';
	}
}
