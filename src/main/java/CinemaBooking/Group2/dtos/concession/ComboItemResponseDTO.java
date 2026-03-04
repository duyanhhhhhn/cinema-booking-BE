package CinemaBooking.Group2.dtos.concession;

import java.math.BigDecimal;

public class ComboItemResponseDTO {
	private int id;
	private String name;
	private int quantity;
	private BigDecimal price;
	
	public ComboItemResponseDTO() {}
	
	public ComboItemResponseDTO(int productId, String productName, int quantity,BigDecimal price) {
		this.id = productId;
		this.name = productName;
		this.quantity = quantity;
		this.price=price;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setProductId(int productId) {
		this.id = productId;
	}

	public String getProductName() {
		return name;
	}

	public void setProductName(String productName) {
		this.name = productName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}