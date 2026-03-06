package CinemaBooking.Group2.dtos.concession;

import java.math.BigDecimal;
import java.time.LocalDate;

import CinemaBooking.Group2.models.Enum.DiscountType;

public class VoucherResponseDTO {	
	private int id;
    private String code;
    private String description;
    private DiscountType discountType; // PERCENT or AMOUNT
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private LocalDate startAt;
    private LocalDate endAt;
    private int usageLimit;
    private int usedCount;
	private String message;
    private boolean isSuccess;
	
    
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public DiscountType getDiscountType() {
		return discountType;
	}
	public void setDiscountType(DiscountType discountType) {
		this.discountType = discountType;
	}
	public BigDecimal getDiscountValue() {
		return discountValue;
	}
	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}
	public BigDecimal getMinOrderAmount() {
		return minOrderAmount;
	}
	public void setMinOrderAmount(BigDecimal minOrderAmount) {
		this.minOrderAmount = minOrderAmount;
	}
	public LocalDate getStartAt() {
		return startAt;
	}
	public void setStartAt(LocalDate startAt) {
		this.startAt = startAt;
	}
	public LocalDate getEndAt() {
		return endAt;
	}
	public void setEndAt(LocalDate endAt) {
		this.endAt = endAt;
	}
	public int getUsageLimit() {
		return usageLimit;
	}
	public void setUsageLimit(int usageLimit) {
		this.usageLimit = usageLimit;
	}
	public int getUsedCount() {
		return usedCount;
	}
	public void setUsedCount(int usedCount) {
		this.usedCount = usedCount;
	}
	
	/**
	 * @param id
	 * @param code
	 * @param description
	 * @param discountType
	 * @param discountValue
	 * @param minOrderAmount
	 * @param startAt
	 * @param endAt
	 * @param usageLimit
	 * @param usedCount
	 * @param createdAt
	 */
	public VoucherResponseDTO(int id, String code, String description, DiscountType discountType, BigDecimal discountValue,
			BigDecimal minOrderAmount, LocalDate startAt, LocalDate endAt, int usageLimit, int usedCount,
			LocalDate createdAt) {
		super();
		this.id = id;
		this.code = code;
		this.description = description;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.minOrderAmount = minOrderAmount;
		this.startAt = startAt;
		this.endAt = endAt;
		this.usageLimit = usageLimit;
		this.usedCount = usedCount;
	}
	/**
	 * 
	 */
	public VoucherResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
    
}
