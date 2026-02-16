package CinemaBooking.Group2.dtos.concession;

public class ComboItemResponseDTO {
	private int id;
	private int combo_id;
	public ComboItemResponseDTO(int id, int combo_id, String name, String description, String image_url, int stock,
			int quantity, int is_active) {
		super();
		this.id = id;
		this.combo_id = combo_id;
		this.name = name;
		this.description = description;
		this.image_url = image_url;
		this.stock = stock;
		this.quantity = quantity;
		this.is_active = is_active;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCombo_id() {
		return combo_id;
	}
	public void setCombo_id(int combo_id) {
		this.combo_id = combo_id;
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
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int isIs_active() {
		return is_active;
	}
	public void setIs_active(int is_active) {
		this.is_active = is_active;
	}
	private String name;
	private String description;
	private String image_url;
	private int stock;
	private int quantity;
	private int is_active;
	public ComboItemResponseDTO() {}
}
