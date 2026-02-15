package CinemaBooking.Group2.dtos.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class WalkInBookingResponse {
    
    private Integer bookingId;
    private String bookingCode;
    private Integer showtimeId;
    private String movieTitle;
    private String cinemaName;
    private String roomName;
    private LocalDateTime showtime;
    private List<SeatInfo> seats;
    private List<ComboInfo> combos;
    private String voucherCode;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private Integer createdByStaffId;
    private String createdByStaffName;
    
    public static class SeatInfo {
        private Integer seatId;
        private String seatName;
        private String seatType;
        private BigDecimal price;
        
        public SeatInfo() {}
        
        public SeatInfo(Integer seatId, String seatName, String seatType, BigDecimal price) {
            this.seatId = seatId;
            this.seatName = seatName;
            this.seatType = seatType;
            this.price = price;
        }

        public Integer getSeatId() {
            return seatId;
        }

        public void setSeatId(Integer seatId) {
            this.seatId = seatId;
        }

        public String getSeatName() {
            return seatName;
        }

        public void setSeatName(String seatName) {
            this.seatName = seatName;
        }

        public String getSeatType() {
            return seatType;
        }

        public void setSeatType(String seatType) {
            this.seatType = seatType;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
    
    public static class ComboInfo {
        private Integer comboId;
        private String comboName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        
        public ComboInfo() {}
        
        public ComboInfo(Integer comboId, String comboName, Integer quantity, BigDecimal unitPrice) {
            this.comboId = comboId;
            this.comboName = comboName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }

        public Integer getComboId() {
            return comboId;
        }

        public void setComboId(Integer comboId) {
            this.comboId = comboId;
        }

        public String getComboName() {
            return comboName;
        }

        public void setComboName(String comboName) {
            this.comboName = comboName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }
    }

    // Getters and Setters
    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public Integer getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Integer showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public LocalDateTime getShowtime() {
        return showtime;
    }

    public void setShowtime(LocalDateTime showtime) {
        this.showtime = showtime;
    }

    public List<SeatInfo> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatInfo> seats) {
        this.seats = seats;
    }

    public List<ComboInfo> getCombos() {
        return combos;
    }

    public void setCombos(List<ComboInfo> combos) {
        this.combos = combos;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedByStaffId() {
        return createdByStaffId;
    }

    public void setCreatedByStaffId(Integer createdByStaffId) {
        this.createdByStaffId = createdByStaffId;
    }

    public String getCreatedByStaffName() {
        return createdByStaffName;
    }

    public void setCreatedByStaffName(String createdByStaffName) {
        this.createdByStaffName = createdByStaffName;
    }
}