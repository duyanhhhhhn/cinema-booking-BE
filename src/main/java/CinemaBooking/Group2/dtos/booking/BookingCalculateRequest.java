package CinemaBooking.Group2.dtos.booking;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BookingCalculateRequest {
    
    @NotNull(message = "Showtime ID is required")
    @Positive(message = "Showtime ID must be positive")
    private Integer showtimeId;
    
    @NotNull(message = "Seat IDs are required")
    private List<Integer> seatIds;
    
    private List<ComboItem> combos;
    
    private String voucherCode;
    
    public static class ComboItem {
        @NotNull(message = "Combo ID is required")
        @Positive(message = "Combo ID must be positive")
        private Integer comboId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;

        public Integer getComboId() {
            return comboId;
        }

        public void setComboId(Integer comboId) {
            this.comboId = comboId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    public Integer getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Integer showtimeId) {
        this.showtimeId = showtimeId;
    }

    public List<Integer> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Integer> seatIds) {
        this.seatIds = seatIds;
    }

    public List<ComboItem> getCombos() {
        return combos;
    }

    public void setCombos(List<ComboItem> combos) {
        this.combos = combos;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}
