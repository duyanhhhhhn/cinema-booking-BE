package CinemaBooking.Group2.dtos.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class InvoiceListResponse {

    private int bookingId;
    private String bookingCode;
    private String invoiceNumber;
    private String customerName;
    private String customerEmail;
    private String movieTitle;
    private String cinemaName;

    @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "HH:mm - dd/MM/yyyy")
    private LocalDateTime paidAt;

    private String paymentStatus;
    private String paymentStatusLabel;
    private String paymentMethod;
    private String paymentMethodLabel;
    private BigDecimal totalPrice;
    private String seatCodes;
    private int seatCount;

    public InvoiceListResponse() {}

    // Getters & Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getCinemaName() { return cinemaName; }
    public void setCinemaName(String cinemaName) { this.cinemaName = cinemaName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

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

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getSeatCodes() { return seatCodes; }
    public void setSeatCodes(String seatCodes) { this.seatCodes = seatCodes; }

    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }
}
