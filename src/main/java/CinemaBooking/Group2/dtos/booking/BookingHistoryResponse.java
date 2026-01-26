package CinemaBooking.Group2.dtos.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingHistoryResponse {
	// 1. Thông tin định danh (để tạo QR Code và link chi tiết)
	private int id;
	private String bookingCode;
	// 2. Thông tin Phim & Rạp (Header của vé)
	private int movieId;
	private String movieTitle;
	private String posterUrl;
	private String cinemaName;
	private String roomName;
	// 3. Thông tin Suất chiếu (Dòng thông tin giữa)
	private LocalDateTime startTime;
	// 4. Thông tin Ghế & Combo (Đã xử lý chuỗi)
	private String seats;
	private String combos;
	// 5. Thanh toán & Trạng thái
	private BigDecimal totalPrice;
	private String status;          // VD: "PAID", "USED", "CANCELLED" (để FE đổi màu nút)
    private String statusLabel;

    public BookingHistoryResponse() {
        // no-arg constructor for row mapping
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getCombos() {
        return combos;
    }

    public void setCombos(String combos) {
        this.combos = combos;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }
}