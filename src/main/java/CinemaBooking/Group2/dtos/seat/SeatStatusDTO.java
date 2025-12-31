package CinemaBooking.Group2.dtos.seat;

import java.math.BigDecimal;

import CinemaBooking.Group2.models.Seat.SeatType;

public class SeatStatusDTO {
    
    public enum SeatStatus {
        AVAILABLE,
        SOLD,
        HELD
    }
    
    private int seatId;
    private String seatCode;
    private SeatType seatType;
    private BigDecimal extraPrice;
    private SeatStatus status;
    private Integer heldByCurrentUser;
    
    public SeatStatusDTO() {
    }
    
    public SeatStatusDTO(int seatId, String seatCode, SeatType seatType, BigDecimal extraPrice, SeatStatus status) {
        this.seatId = seatId;
        this.seatCode = seatCode;
        this.seatType = seatType;
        this.extraPrice = extraPrice;
        this.status = status;
    }

    // Getters and Setters
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

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public BigDecimal getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(BigDecimal extraPrice) {
        this.extraPrice = extraPrice;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public Integer getHeldByCurrentUser() {
        return heldByCurrentUser;
    }

    public void setHeldByCurrentUser(Integer heldByCurrentUser) {
        this.heldByCurrentUser = heldByCurrentUser;
    }
}