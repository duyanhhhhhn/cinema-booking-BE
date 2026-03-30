package CinemaBooking.Group2.dtos.concession;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

public class ProductRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private MultipartFile bannerFile;

    public ProductRequestDTO() {
    }

    public ProductRequestDTO(String name, String description, BigDecimal price, Integer stock, MultipartFile bannerFile) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.bannerFile = bannerFile;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public MultipartFile getBannerFile() {
        return bannerFile;
    }

    public void setBannerFile(MultipartFile bannerFile) {
        this.bannerFile = bannerFile;
    }
}
