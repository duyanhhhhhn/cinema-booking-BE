package CinemaBooking.Group2.dtos.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class InvoiceResponse {

    // ===== THÔNG TIN HOÁ ĐƠN =====
    private int bookingId;
    private String bookingCode;         // VD: BK00000001
    private String invoiceNumber;       // VD: INV-BK00000001

    @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
    private LocalDateTime paidAt;

    private String paymentStatus;       // PAID, PENDING, FAILED, CANCELLED
    private String paymentStatusLabel;  // Đã thanh toán, Chờ thanh toán...
    private String paymentMethod;       // CASH, MOMO, ZALOPAY, VNPAY, STRIPE
    private String paymentMethodLabel;  // Tiền mặt, Ví MoMo...

    // ===== THÔNG TIN KHÁCH HÀNG =====
    private Integer customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // ===== THÔNG TIN PHIM / SUẤT CHIẾU =====
    private String movieTitle;
    private String posterUrl;
    private String format;              // 2D, 3D, IMAX...
    private int durationMinutes;
    private String cinemaName;
    private String cinemaAddress;
    private String roomName;

    @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
    private LocalDateTime endTime;

    // ===== CHI TIẾT HOÁ ĐƠN =====
    private String seatCodes;            // "G7, G8"
    private BigDecimal discountAmount;   // Giảm giá từ voucher
    private String voucherCode;          // Mã voucher (nếu có)
    private BigDecimal totalPrice;       // Tổng tiền thực tế

    private List<InvoiceLineItem> lineItems; // Các mục chi tiết

    // ===== DANH SÁCH VÉ & TRẠNG THÁI IN =====
    private List<TicketPrintInfo> tickets;

    // ===== INNER CLASS =====
    public static class InvoiceLineItem {
        private String description;     // "Vé (G7, G8)", "Combo Bắp Lớn x1"
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        public InvoiceLineItem() {}

        public InvoiceLineItem(String description, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }

    public static class TicketPrintInfo {
        private String seatCode;        // "G7"
        private String seatType;        // "STANDARD", "VIP", "COUPLE"
        private String ticketCode;      // "TK-G7-UID1"
        private BigDecimal seatPrice;   // Giá vé của ghế này
        private boolean isPrinted;      // Đã in vé chưa
        private String printedByName;   // Tên nhân viên in vé (nếu có)

        @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
        private java.time.LocalDateTime printedAt; // Thời điểm in vé

        public TicketPrintInfo() {}

        // Getters & Setters
        public String getSeatCode() { return seatCode; }
        public void setSeatCode(String seatCode) { this.seatCode = seatCode; }

        public String getSeatType() { return seatType; }
        public void setSeatType(String seatType) { this.seatType = seatType; }

        public String getTicketCode() { return ticketCode; }
        public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

        public BigDecimal getSeatPrice() { return seatPrice; }
        public void setSeatPrice(BigDecimal seatPrice) { this.seatPrice = seatPrice; }

        public boolean isPrinted() { return isPrinted; }
        public void setPrinted(boolean isPrinted) { this.isPrinted = isPrinted; }

        public String getPrintedByName() { return printedByName; }
        public void setPrintedByName(String printedByName) { this.printedByName = printedByName; }

        public java.time.LocalDateTime getPrintedAt() { return printedAt; }
        public void setPrintedAt(java.time.LocalDateTime printedAt) { this.printedAt = printedAt; }
    }

    // ===== GETTERS & SETTERS =====
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentStatusLabel() { return paymentStatusLabel; }
    public void setPaymentStatusLabel(String paymentStatusLabel) { this.paymentStatusLabel = paymentStatusLabel; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentMethodLabel() { return paymentMethodLabel; }
    public void setPaymentMethodLabel(String paymentMethodLabel) { this.paymentMethodLabel = paymentMethodLabel; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getCinemaName() { return cinemaName; }
    public void setCinemaName(String cinemaName) { this.cinemaName = cinemaName; }

    public String getCinemaAddress() { return cinemaAddress; }
    public void setCinemaAddress(String cinemaAddress) { this.cinemaAddress = cinemaAddress; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getSeatCodes() { return seatCodes; }
    public void setSeatCodes(String seatCodes) { this.seatCodes = seatCodes; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public List<InvoiceLineItem> getLineItems() { return lineItems; }
    public void setLineItems(List<InvoiceLineItem> lineItems) { this.lineItems = lineItems; }

    public List<TicketPrintInfo> getTickets() { return tickets; }
    public void setTickets(List<TicketPrintInfo> tickets) { this.tickets = tickets; }
}