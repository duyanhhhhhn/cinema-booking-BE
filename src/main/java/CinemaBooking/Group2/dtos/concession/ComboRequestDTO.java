package CinemaBooking.Group2.dtos.concession;

import org.springframework.web.multipart.MultipartFile;

public class ComboRequestDTO {
    private String name;
    private String description;
    private String price;
    private String item;
    private MultipartFile bannerFile;
    private boolean priceProvided;

    public ComboRequestDTO() {
    }

    public ComboRequestDTO(String name, String description, String price, String item, MultipartFile bannerFile) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.item = item;
        this.bannerFile = bannerFile;
        this.priceProvided = price != null;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        this.priceProvided = true;
    }

    public String getComboPrice() {
        return price;
    }

    public void setComboPrice(String comboPrice) {
        this.price = comboPrice;
        this.priceProvided = true;
    }

    public String getSalePrice() {
        return price;
    }

    public void setSalePrice(String salePrice) {
        this.price = salePrice;
        this.priceProvided = true;
    }

    public String getDiscountPrice() {
        return price;
    }

    public void setDiscountPrice(String discountPrice) {
        this.price = discountPrice;
        this.priceProvided = true;
    }

    public String getFinalPrice() {
        return price;
    }

    public void setFinalPrice(String finalPrice) {
        this.price = finalPrice;
        this.priceProvided = true;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getComboItem() {
        return item;
    }

    public void setComboItem(String comboItem) {
        this.item = comboItem;
    }

    public MultipartFile getBannerFile() {
        return bannerFile;
    }

    public void setBannerFile(MultipartFile bannerFile) {
        this.bannerFile = bannerFile;
    }

    public boolean isPriceProvided() {
        return priceProvided;
    }
}
