package CinemaBooking.Group2.dtos.booking;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BookingCreateRequest {
    
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Integer userId;
    
    @NotNull(message = "Showtime ID is required")
    @Positive(message = "Showtime ID must be positive")
    private Integer showtimeId;
    
    @NotNull(message = "Seat IDs are required")
    private List<Integer> seatIds;
    
    private List<ComboItem> combos;
    
    private List<ProductItem> products;
    
    private String voucherCode;
    
    private String paymentMethod; // CASH, MOMO, ZALOPAY, VNPAY, STRIPE
    // Optional bank code / payment channel (e.g. for ATM card payment)
    private String bankCode;
    
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
    
    public static class ProductItem {
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Integer productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public List<ProductItem> getProducts() {
        return products;
    }

    public void setProducts(List<ProductItem> products) {
        this.products = products;
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}