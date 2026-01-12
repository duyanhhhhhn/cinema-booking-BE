package CinemaBooking.Group2.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.models.Booking;
import CinemaBooking.Group2.models.BookingSeat;

@Service
public class EmailService {

    @Autowired
    private ResendClient resendClient;

    public void sendOtp(String to, String otp) {
        String html = "<h3>Mã OTP của bạn là:</h3>" +
                      "<h1 style='color:red;'>" + otp + "</h1>" +
                      "<p>Có hiệu lực trong 5 phút.</p>";

        resendClient.sendEmail(to, "Xác thực đăng ký tài khoản", html);
    }

    public void sendTickets(String to, Booking booking, List<BookingSeat> seats) {
        if (to == null || to.isBlank()) return;

        StringBuilder html = new StringBuilder();
        html.append("<h2>Your Booking: ").append(booking.getBookingCode()).append("</h2>");
        html.append("<p>Showtime ID: ").append(booking.getShowtimeId()).append("</p>");
        html.append("<p>Total: <strong>").append(booking.getTotalPrice()).append(" VND</strong></p>");
        html.append("<p>Seats:</p>");
        html.append("<ul>");
        for (BookingSeat s : seats) {
            html.append("<li>")
                .append("Seat ID: ").append(s.getSeatId())
                .append(" - Ticket: <strong>").append(s.getTicketCode()).append("</strong>")
                .append("</li>");
        }
        html.append("</ul>");
        if (booking.getPaidAt() != null) {
            html.append("<p>Paid at: ").append(booking.getPaidAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>");
        }

        resendClient.sendEmail(to, "Your Cinema Tickets - " + booking.getBookingCode(), html.toString());
    }
}