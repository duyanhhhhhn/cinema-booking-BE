package CinemaBooking.Group2.dtos.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO chứa toàn bộ thông tin cần thiết để gửi email xác nhận đặt vé
 */
public class BookingEmailData {

    private String bookingCode;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String movieTitle;
    private LocalDateTime showtime;
    private String cinemaName;
    private String cinemaAddress;
    private String roomName;
    private int ticketCount;
    private String seatCodes;
    private String comboDetails;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

    // Getters and Setters
    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public LocalDateTime getShowtime() {
        return showtime;
    }

    public void setShowtime(LocalDateTime showtime) {
        this.showtime = showtime;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getCinemaAddress() {
        return cinemaAddress;
    }

    public void setCinemaAddress(String cinemaAddress) {
        this.cinemaAddress = cinemaAddress;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

    public String getSeatCodes() {
        return seatCodes;
    }

    public void setSeatCodes(String seatCodes) {
        this.seatCodes = seatCodes;
    }

    public String getComboDetails() {
        return comboDetails;
    }

    public void setComboDetails(String comboDetails) {
        this.comboDetails = comboDetails;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
