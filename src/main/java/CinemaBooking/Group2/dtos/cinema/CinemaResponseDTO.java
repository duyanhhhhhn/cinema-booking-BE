package CinemaBooking.Group2.dtos.cinema;

import java.time.LocalDateTime;

public class CinemaResponseDTO {

    private int id;
    private String name;
    private String address;
    private String phone;
    private String description;
    private int isActive;
    private String imageUrl;
    private LocalDateTime createdAt;
    
    

    /**
	 * @return the id
	 */
	public int getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
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
	 * @return the isActive
	 */
	public int getIsActive() {
		return isActive;
	}



	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(int isActive) {
		this.isActive = isActive;
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



	/**
	 * @return the createdAt
	 */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}



	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}



	public CinemaResponseDTO(
            int id,
            String name,
            String address,
            String phone,
            String description,
            int isActive,
            String imageUrl,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.isActive = isActive;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }
}

