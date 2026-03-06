package CinemaBooking.Group2.dtos.booking;

import java.math.BigDecimal;
import java.util.List;

public class BookingCalculateResponse {
    
    private BigDecimal basePrice;           // Tổng giá vé cơ bản
    private BigDecimal seatExtraPrice;      // Phụ thu ghế VIP
    private BigDecimal comboPrice;          // Tổng giá combo
    private BigDecimal productPrice;        // Tổng giá sản phẩm lẻ
    private BigDecimal holidaySurcharge;    // Phụ thu lễ
    private BigDecimal subtotal;            // Tổng trước giảm giá
    private BigDecimal discountAmount;      // Số tiền giảm giá từ voucher
    private BigDecimal totalPrice;          // Tổng tiền cuối cùng
    private String voucherCode;             // Mã voucher đã áp dụng
    private PriceBreakdown breakdown;
    
    public static class PriceBreakdown {
        private List<SeatDetail> seats;
        private List<ComboDetail> combos;
        private List<ProductDetail> products;
        private HolidayDetail holiday;
        
        public static class SeatDetail {
            private Integer seatId;
            private String seatCode;
            private String seatType;
            private BigDecimal basePrice;
            private BigDecimal extraPrice;
            private BigDecimal totalPrice;
            
            public Integer getSeatId() {
                return seatId;
            }
            public void setSeatId(Integer seatId) {
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
            public BigDecimal getBasePrice() {
                return basePrice;
            }
            public void setBasePrice(BigDecimal basePrice) {
                this.basePrice = basePrice;
            }
            public BigDecimal getExtraPrice() {
                return extraPrice;
            }
            public void setExtraPrice(BigDecimal extraPrice) {
                this.extraPrice = extraPrice;
            }
            public BigDecimal getTotalPrice() {
                return totalPrice;
            }
            public void setTotalPrice(BigDecimal totalPrice) {
                this.totalPrice = totalPrice;
            }
        }
        
        public static class ComboDetail {
            private Integer comboId;
            private String comboName;
            private Integer quantity;
            private BigDecimal unitPrice;
            private BigDecimal totalPrice;
            
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
        
        public static class ProductDetail {
            private Integer productId;
            private String productName;
            private Integer quantity;
            private BigDecimal unitPrice;
            private BigDecimal totalPrice;
            
            public Integer getProductId() {
                return productId;
            }
            public void setProductId(Integer productId) {
                this.productId = productId;
            }
            public String getProductName() {
                return productName;
            }
            public void setProductName(String productName) {
                this.productName = productName;
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
        
        public static class HolidayDetail {
            private String holidayName;
            private String adjustmentType;
            private BigDecimal value;
            private BigDecimal surchargeAmount;
            
            public String getHolidayName() {
                return holidayName;
            }
            public void setHolidayName(String holidayName) {
                this.holidayName = holidayName;
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
            public BigDecimal getSurchargeAmount() {
                return surchargeAmount;
            }
            public void setSurchargeAmount(BigDecimal surchargeAmount) {
                this.surchargeAmount = surchargeAmount;
            }
        }
        
        public List<SeatDetail> getSeats() {
            return seats;
        }
        public void setSeats(List<SeatDetail> seats) {
            this.seats = seats;
        }
        public List<ComboDetail> getCombos() {
            return combos;
        }
        public void setCombos(List<ComboDetail> combos) {
            this.combos = combos;
        }
        public List<ProductDetail> getProducts() {
            return products;
        }
        public void setProducts(List<ProductDetail> products) {
            this.products = products;
        }
        public HolidayDetail getHoliday() {
            return holiday;
        }
        public void setHoliday(HolidayDetail holiday) {
            this.holiday = holiday;
        }
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getSeatExtraPrice() {
        return seatExtraPrice;
    }

    public void setSeatExtraPrice(BigDecimal seatExtraPrice) {
        this.seatExtraPrice = seatExtraPrice;
    }

    public BigDecimal getComboPrice() {
        return comboPrice;
    }

    public void setComboPrice(BigDecimal comboPrice) {
        this.comboPrice = comboPrice;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public BigDecimal getHolidaySurcharge() {
        return holidaySurcharge;
    }

    public void setHolidaySurcharge(BigDecimal holidaySurcharge) {
        this.holidaySurcharge = holidaySurcharge;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
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

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public PriceBreakdown getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(PriceBreakdown breakdown) {
        this.breakdown = breakdown;
    }
}
