package CinemaBooking.Group2.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceAdjustment {

	public enum AdjustmentType {
	    AMOUNT,
	    PERCENT
	}
	
	private int id;
    private String name;
    private AdjustmentType adjustmentType;
    private BigDecimal value;
    private String applyOnDays; // 
    private LocalDate startDate;
    private LocalDate endDate;
    private int active;
	public int getId() {
		return id;
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
	public AdjustmentType getAdjustmentType() {
		return adjustmentType;
	}
	public void setAdjustmentType(AdjustmentType adjustmentType) {
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
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	/**
	 * @param id
	 * @param name
	 * @param adjustmentType
	 * @param value
	 * @param applyOnDays
	 * @param startDate
	 * @param endDate
	 * @param active
	 */
	public PriceAdjustment(int id, String name, AdjustmentType adjustmentType, BigDecimal value, String applyOnDays,
			LocalDate startDate, LocalDate endDate, int active) {
		super();
		this.id = id;
		this.name = name;
		this.adjustmentType = adjustmentType;
		this.value = value;
		this.applyOnDays = applyOnDays;
		this.startDate = startDate;
		this.endDate = endDate;
		this.active = active;
	}
	/**
	 * 
	 */
	public PriceAdjustment() {
		super();
		// TODO Auto-generated constructor stub
	}

    
}
