package CinemaBooking.Group2.dtos.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class BookingDetailResponse {

    // ==========================================
    // 1. THÔNG TIN VÉ & QR (Phần bên phải UI)
    // ==========================================
	private int id;
    private String bookingCode;      // VD: "MAVE123456"
    private String qrData;           // Dữ liệu để tạo QR Code (thường là bookingCode)
    private String status;           // VD: PAID, PENDING (Enum gốc)
    private String statusLabel;      // VD: "CHƯA SỬ DỤNG", "ĐÃ SỬ DỤNG" (Hiển thị UI)
    private boolean isCheckin;       // Để đổi màu tag trạng thái (xanh/xám)

    // ==========================================
    // 2. THÔNG TIN PHIM (Phần header bên trái)
    // ==========================================
    private String movieTitle;       // "Oppenheimer"
    private String posterUrl;        // Link ảnh poster
    private String tagline;          // "Thế giới sẽ nhớ mãi..." (Lấy từ short_description)
    private String ageRating;        // "T18" (Lưu ý: DB của bạn đang thiếu cột này)
    private String format;           // "IMAX 2D" (Lấy từ bảng movie.format hoặc screen type)
    private int durationMinutes;     // 180 phút

    // ==========================================
    // 3. THÔNG TIN RẠP & SUẤT CHIẾU
    // ==========================================
    private String cinemaName;       // "CinemaHub Quận 1"
    private String cinemaAddress;    // "Tầng 3, Bitexco..."
    private String roomName;         // "PHÒNG CHIẾU 04 (IMAX)"
    
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime startTime; // 19:45
    
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime endTime;   // 22:45
    
    @JsonFormat(pattern = "EEEE, dd/MM/yyyy") 
    private LocalDateTime showDate;  // "Thứ sáu, 20/12/2023" (Dùng chung startTime để format)

    private String seatCodes;        // "G7, G8" (Chuỗi danh sách ghế)

    // ==========================================
    // 4. CHI TIẾT THANH TOÁN (Phần dưới cùng)
    // ==========================================
    @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
    private LocalDateTime createdAt; // "14:22 - 18/12/2023"
    private String paymentMethod;    // "Thanh toán Ví MoMo"
    private BigDecimal totalPrice;   // 255.000đ [@JsonFormat(pattern = "HH:mm - dd/MM/yyyy")    private LocalDateTime createdAt; // "14:22 - 18/12/2023"    private String paymentMethod;    // "Thanh toán Ví MoMo"    private BigDecimal totalPrice;   // 255.000đ] x

    // Danh sách các mục đã mua (Vé + Bắp nước) để hiển thị dòng "1x Combo...", "2x Coca..."
    private List<BillItem> items; 

    // Constructor rỗng
    public BookingDetailResponse() {}

    // ==========================================
    // INNER CLASS CHO DANH SÁCH MÓN (Bill Item)
    // ==========================================
    public static class BillItem {
        private String name;       // "Combo Bắp Rang Bơ...", "Vé Người Lớn (G7, G8)"
        private int quantity;      // 1, 2
        private BigDecimal price;  // Giá tổng của mục đó (VD: 85.000đ)

        public BillItem() {}

        public BillItem(String name, int quantity, BigDecimal price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters & Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    // --- GETTERS & SETTERS CHO CLASS CHÍNH (Bạn tự generate nhé) ---
    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQrData() { return qrData; }
    public void setQrData(String qrData) { this.qrData = qrData; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }

    public boolean isCheckin() { return isCheckin; }
    public void setIsCheckin(boolean isCheckin) { this.isCheckin = isCheckin; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

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

    public LocalDateTime getShowDate() { return showDate; }
    public void setShowDate(LocalDateTime showDate) { this.showDate = showDate; }

    public String getSeatCodes() { return seatCodes; }
    public void setSeatCodes(String seatCodes) { this.seatCodes = seatCodes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }

}