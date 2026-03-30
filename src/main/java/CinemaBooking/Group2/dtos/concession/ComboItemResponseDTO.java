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
	private String image_url;
	
	public int getProductId() {
		return productId;
	}

	public ComboItemResponseDTO() {}
	
	public ComboItemResponseDTO(int id,int productId, String productName, int quantity,BigDecimal price,String image_url) {
		this.id = id;
		this.name = productName;
		this.quantity = quantity;
		this.price=price;
		this.productId = productId;
		this.image_url = image_url;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setProductId(int productId) {
		this.productId = productId;
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
