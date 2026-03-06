package CinemaBooking.Group2.dtos.concession;

import java.math.BigDecimal;

public class ComboItemResponseDTO {
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int id;
	private String name;
	private int quantity;
	private BigDecimal price;
	private int productId;
	
	public int getProductId() {
		return productId;
	}

	public ComboItemResponseDTO() {}
	
	public ComboItemResponseDTO(int id,int productId, String productName, int quantity,BigDecimal price) {
		this.id = id;
		this.name = productName;
		this.quantity = quantity;
		this.price=price;
		this.productId = productId;
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