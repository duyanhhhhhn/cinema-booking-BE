package CinemaBooking.Group2.dtos.cinema;

import org.springframework.web.multipart.MultipartFile;

public class CinemaRequestDTO {
    private String name;
    private String address;
    private String phone;
    private String description;
    private Integer isActive;
    private MultipartFile imageUrl;  

    // ===== GETTERS & SETTERS =====
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getIsActive() { return isActive; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }

    public MultipartFile getImageUrl() { return imageUrl; }
    public void setImageUrl(MultipartFile imageUrl) { this.imageUrl = imageUrl; }

    // ===== CONSTRUCTORS =====
    public CinemaRequestDTO() { super(); }

    public CinemaRequestDTO(String name, String address, String phone, String description, Integer isActive,
                            MultipartFile imageUrl) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.isActive = isActive;
        this.imageUrl = imageUrl;
    }
}
