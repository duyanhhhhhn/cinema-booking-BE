package CinemaBooking.Group2.models;

import java.time.LocalDateTime;

public class Cinema {

	private int id;
    private String name;
    private String address;
    private String phone;
    private String description;  // có thể null
    private int isActive;
    private String imageUrl;
    private LocalDateTime createdAt;
	public int getId() {
		return id;
	}
	/**
	 * @param id
	 * @param name
	 * @param address
	 * @param phone
	 * @param description
	 * @param isActive
	 * @param imageUrl
	 * @param createdAt
	 */
	public Cinema(int id, String name, String address, String phone, String description, int isActive, String imageUrl,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.description = description;
		this.isActive = isActive;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
	}
	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
	public Cinema() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
