package CinemaBooking.Group2.dtos.ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response khi in vé thành công")
public class PrintTicketResponse {

    @Schema(description = "Mã booking")
    private String bookingCode;

    @Schema(description = "Tên phim")
    private String movieTitle;

    @Schema(description = "Tên rạp")
    private String cinemaName;

    @Schema(description = "Tên phòng chiếu")
    private String roomName;

    @Schema(description = "Thời gian bắt đầu chiếu")
    @JsonFormat(pattern = "HH:mm dd/MM/yyyy")
    private LocalDateTime startTime;

    @Schema(description = "Danh sách vé đã in")
    private List<PrintedTicket> printedTickets;

    @Schema(description = "Số vé đã in")
    private int totalPrinted;

    @Schema(description = "Thời gian in")
    @JsonFormat(pattern = "HH:mm dd/MM/yyyy")
    private LocalDateTime printedAt;

    @Schema(description = "Mã stamp in (để kiểm tra đã in)")
    private String printStamp;

    public PrintTicketResponse() {}

    // Inner class cho thông tin từng vé đã in
    public static class PrintedTicket {
        @Schema(description = "Mã vé")
        private String ticketCode;

        @Schema(description = "Mã ghế")
        private String seatCode;

        @Schema(description = "Loại ghế (VIP, STANDARD, COUPLE)")
        private String seatType;

        @Schema(description = "Giá vé")
        private BigDecimal price;

        @Schema(description = "Dữ liệu QR Code")
        private String qrData;

        public PrintedTicket() {}

        public PrintedTicket(String ticketCode, String seatCode, String seatType, BigDecimal price, String qrData) {
            this.ticketCode = ticketCode;
            this.seatCode = seatCode;
            this.seatType = seatType;
            this.price = price;
            this.qrData = qrData;
        }

        public String getTicketCode() { return ticketCode; }
        public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

        public String getSeatCode() { return seatCode; }
        public void setSeatCode(String seatCode) { this.seatCode = seatCode; }

        public String getSeatType() { return seatType; }
        public void setSeatType(String seatType) { this.seatType = seatType; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public String getQrData() { return qrData; }
        public void setQrData(String qrData) { this.qrData = qrData; }
    }

    // Getters & Setters
    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getCinemaName() { return cinemaName; }
    public void setCinemaName(String cinemaName) { this.cinemaName = cinemaName; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public List<PrintedTicket> getPrintedTickets() { return printedTickets; }
    public void setPrintedTickets(List<PrintedTicket> printedTickets) { this.printedTickets = printedTickets; }

    public int getTotalPrinted() { return totalPrinted; }
    public void setTotalPrinted(int totalPrinted) { this.totalPrinted = totalPrinted; }

    public LocalDateTime getPrintedAt() { return printedAt; }
    public void setPrintedAt(LocalDateTime printedAt) { this.printedAt = printedAt; }

    public String getPrintStamp() { return printStamp; }
    public void setPrintStamp(String printStamp) { this.printStamp = printStamp; }
}
