package CinemaBooking.Group2.models;

public class ComboItem {

	private int id;
    private int comboId;
    private int productId;
    private int quantity;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getComboId() {
		return comboId;
	}
	public void setComboId(int comboId) {
		this.comboId = comboId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	/**
	 * @param id
	 * @param comboId
	 * @param productId
	 * @param quantity
	 */
	public ComboItem(int id, int comboId, int productId, int quantity) {
		super();
		this.id = id;
		this.comboId = comboId;
		this.productId = productId;
		this.quantity = quantity;
	}
	/**
	 * 
	 */
	public ComboItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	
    
    

}
