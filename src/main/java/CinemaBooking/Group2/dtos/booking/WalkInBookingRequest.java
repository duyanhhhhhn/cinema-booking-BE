package CinemaBooking.Group2.dtos.booking;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

public class WalkInBookingRequest {
    
    // Booking information
    @NotNull(message = "Showtime ID is required")
    @Positive(message = "Showtime ID must be positive")
    private Integer showtimeId;
    
    @NotNull(message = "Seat IDs are required")
    @NotEmpty(message = "At least one seat must be selected")
    private List<Integer> seatIds;
    
    // Optional concessions
    private List<ComboItem> combos;
    
    // Optional voucher
    private String voucherCode;
    
    // Payment method - only CASH or MOMO allowed for walk-in customers
    @NotEmpty(message = "Payment method is required")
    @Pattern(regexp = "^(CASH|MOMO)$", message = "Payment method must be either CASH or MOMO")
    private String paymentMethod;
    
    // Staff ID who is creating the booking
    @NotNull(message = "Staff ID is required")
    @Positive(message = "Staff ID must be positive")
    private Integer staffId;
    
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

    // Getters and Setters
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }
}