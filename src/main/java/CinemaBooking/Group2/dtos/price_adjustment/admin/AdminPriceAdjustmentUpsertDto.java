package CinemaBooking.Group2.dtos.price_adjustment.admin;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AdminPriceAdjustmentUpsertDto {
    private String name;
    private String adjustmentType; // AMOUNT | PERCENT
    private BigDecimal value;
    private String applyOnDays;    // ví dụ: Sat,Sun
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;

    public AdminPriceAdjustmentUpsertDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getApplyOnDays() {
        return applyOnDays;
    }

    public void setApplyOnDays(String applyOnDays) {
        this.applyOnDays = applyOnDays;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}