package CinemaBooking.Group2.dtos.ticket;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request để in vé tại quầy")
public class PrintTicketRequest {

    @Schema(description = "Mã booking", example = "BK00000001", required = true)
    private String bookingCode;

    @Schema(description = "Danh sách mã vé cần in (nếu null hoặc rỗng sẽ in tất cả vé của booking)", example = "[\"BK00000001-A1\", \"BK00000001-A2\"]")
    private List<String> ticketCodes;

    public PrintTicketRequest() {
    }

    public PrintTicketRequest(String bookingCode, List<String> ticketCodes) {
        this.bookingCode = bookingCode;
        this.ticketCodes = ticketCodes;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public List<String> getTicketCodes() {
        return ticketCodes;
    }

    public void setTicketCodes(List<String> ticketCodes) {
        this.ticketCodes = ticketCodes;
    }
}
