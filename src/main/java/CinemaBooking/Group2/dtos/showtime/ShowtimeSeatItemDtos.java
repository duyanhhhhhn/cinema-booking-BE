package CinemaBooking.Group2.dtos.showtime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShowtimeSeatItemDtos {
    private int seatId;
    private String seatCode;
    private String seatType;
    private BigDecimal extraPrice;
    private String seatStatus;
    public int getSeatId() {
		return seatId;
	}
	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}
	public String getSeatCode() {
		return seatCode;
	}
	public void setSeatCode(String seatCode) {
		this.seatCode = seatCode;
	}
	public String getSeatType() {
		return seatType;
	}
	public void setSeatType(String seatType) {
		this.seatType = seatType;
	}
	public BigDecimal getExtraPrice() {
		return extraPrice;
	}
	public void setExtraPrice(BigDecimal extraPrice) {
		this.extraPrice = extraPrice;
	}
	public String getSeatStatus() {
		return seatStatus;
	}
	public void setSeatStatus(String seatStatus) {
		this.seatStatus = seatStatus;
	}
	public LocalDateTime getHoldExpiresAt() {
		return holdExpiresAt;
	}
	public void setHoldExpiresAt(LocalDateTime holdExpiresAt) {
		this.holdExpiresAt = holdExpiresAt;
	}
	private LocalDateTime holdExpiresAt;	
}
