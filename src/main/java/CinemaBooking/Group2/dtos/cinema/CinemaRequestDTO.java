package CinemaBooking.Group2.dtos.cinema;

public class CinemaRequestDTO {
	private String name;
    private String address;
    private String phone;
    private String description;
    private Integer isActive;
    
	/**
	 * @return the isActive
	 */
	public Integer getIsActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @param name
	 * @param address
	 * @param phone
	 * @param description
	 * @param isActive
	 */
	public CinemaRequestDTO(String name, String address, String phone, String description, Integer isActive) {
		super();
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.description = description;
		this.isActive = isActive;
	}
	public CinemaRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
    
}
