package CinemaBooking.Group2.dtos.price_adjustment.admin;

public class AdminPriceAdjustmentFilterDto {
    private String keyword;
    private Boolean isActive;

    public AdminPriceAdjustmentFilterDto() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}