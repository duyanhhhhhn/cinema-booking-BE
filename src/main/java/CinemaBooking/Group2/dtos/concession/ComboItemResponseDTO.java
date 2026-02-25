package CinemaBooking.Group2.dtos.concession;

public class ComboItemResponseDTO {
	private int id;
	private String name;
	private int quantity;
	
	public ComboItemResponseDTO() {}
	
	public ComboItemResponseDTO(int productId, String productName, int quantity) {
		this.id = productId;
		this.name = productName;
		this.quantity = quantity;
	}

	public int getProductId() {
		return id;
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