package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.booking.BookingEmailData;
import CinemaBooking.Group2.models.Booking;
import CinemaBooking.Group2.models.BookingSeat;
import CinemaBooking.Group2.ultis.QrCodeUtil;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private ResendClient resendClient;

    public void sendOtp(String to, String otp) {
        String html = "<h3>Mã OTP của bạn là:</h3>" +
                "<h1 style='color:red;'>" + otp + "</h1>" +
                "<p>Có hiệu lực trong 5 phút.</p>";

        resendClient.sendEmail(to, "Xác thực đăng ký tài khoản", html);
    }

    /**
     * Gửi email xác nhận đặt vé với template đẹp + QR code
     */
    public void sendBookingConfirmation(String to, BookingEmailData data) {
        if (to == null || to.isBlank())
            return;

        try {
            // Generate QR code URL using public API (base64 inline images are blocked by
            // most email clients)
            String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=180x180&data="
                    + java.net.URLEncoder.encode(data.getBookingCode(), java.nio.charset.StandardCharsets.UTF_8);

            // Format ngày giờ
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String showtimeStr = data.getShowtime() != null ? data.getShowtime().format(dtf) : "N/A";
            String createdAtStr = data.getCreatedAt() != null ? data.getCreatedAt().format(dtf) : "N/A";

            // Format tiền
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            String totalPriceStr = data.getTotalPrice() != null
                    ? currencyFormat.format(data.getTotalPrice())
                    : "0";

            String comboDetails = (data.getComboDetails() != null && !data.getComboDetails().isBlank())
                    ? data.getComboDetails()
                    : "Không có";

            String html = buildBookingEmailHtml(
                    data.getCustomerName(),
                    data.getBookingCode(),
                    qrCodeUrl,
                    data.getMovieTitle(),
                    showtimeStr,
                    data.getCinemaName(),
                    data.getCinemaAddress(),
                    data.getRoomName(),
                    data.getTicketCount(),
                    data.getSeatCodes(),
                    comboDetails,
                    totalPriceStr,
                    data.getCustomerPhone(),
                    data.getCustomerEmail(),
                    createdAtStr);

            resendClient.sendEmail(to, "Xác nhận đặt vé - " + data.getBookingCode(), html);
            logger.info("Sent booking confirmation email for {} to {}", data.getBookingCode(), to);
        } catch (Exception e) {
            logger.error("Failed to send booking confirmation email to {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Phương thức cũ giữ lại để tương thích - gửi email đơn giản
     */
    public void sendTickets(String to, Booking booking, List<BookingSeat> seats) {
        if (to == null || to.isBlank())
            return;

        try {
            // Generate QR code URL using public API (base64 inline images are blocked by
            // most email clients)
            String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=180x180&data="
                    + java.net.URLEncoder.encode(booking.getBookingCode(), java.nio.charset.StandardCharsets.UTF_8);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            String createdAtStr = booking.getCreatedAt() != null
                    ? booking.getCreatedAt().format(dtf)
                    : "N/A";
            String totalPriceStr = booking.getTotalPrice() != null
                    ? currencyFormat.format(booking.getTotalPrice())
                    : "0";

            // Build seat codes
            StringBuilder seatCodes = new StringBuilder();
            for (int i = 0; i < seats.size(); i++) {
                if (i > 0)
                    seatCodes.append(", ");
                seatCodes.append(seats.get(i).getTicketCode());
            }

            String html = buildBookingEmailHtml(
                    "Quý khách",
                    booking.getBookingCode(),
                    qrCodeUrl,
                    "N/A",
                    "N/A",
                    "N/A",
                    "N/A",
                    "N/A",
                    seats.size(),
                    seatCodes.toString(),
                    "N/A",
                    totalPriceStr,
                    "N/A",
                    to,
                    createdAtStr);

            resendClient.sendEmail(to, "Xác nhận đặt vé - " + booking.getBookingCode(), html);
        } catch (Exception e) {
            logger.error("Failed to send tickets email to {}: {}", to, e.getMessage(), e);
        }
    }

    private String buildBookingEmailHtml(
            String customerName,
            String bookingCode,
            String qrBase64,
            String movieTitle,
            String showtime,
            String cinemaName,
            String cinemaAddress,
            String roomName,
            int ticketCount,
            String seatCodes,
            String comboDetails,
            String totalPrice,
            String customerPhone,
            String customerEmail,
            String createdAt) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang='vi'>");
        sb.append("<head><meta charset='UTF-8'/></head>");
        sb.append("<body style='margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,Helvetica,sans-serif;'>");

        // Container
        sb.append(
                "<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f4;'>");
        sb.append("<tr><td align='center' style='padding:20px 0;'>");
        sb.append(
                "<table role='presentation' width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 12px rgba(0,0,0,0.1);'>");

        // Header
        sb.append(
                "<tr><td style='background:linear-gradient(135deg,#1a1a2e 0%,#16213e 50%,#0f3460 100%);padding:30px 40px;text-align:center;'>");
        sb.append(
                "<h1 style='color:#e94560;margin:0;font-size:28px;letter-spacing:2px;'>&#127909; Beta Two Cinema</h1>");
        sb.append("</td></tr>");

        // Greeting
        sb.append("<tr><td style='padding:30px 40px 10px;'>");
        sb.append("<p style='font-size:16px;color:#333;margin:0;'>Xin chào <strong>").append(esc(customerName))
                .append("</strong>,</p>");
        sb.append(
                "<p style='font-size:14px;color:#666;margin:8px 0 0;'>Cảm ơn bạn đã sử dụng dịch vụ của hệ thống rạp chiếu phim!</p>");
        sb.append("<p style='font-size:14px;color:#666;margin:4px 0 0;'>Hệ thống xác nhận bạn đã đặt vé tại <strong>")
                .append(esc(cinemaName)).append("</strong> thành công lúc <strong>").append(esc(createdAt))
                .append("</strong>.</p>");
        sb.append("</td></tr>");

        // Booking Code + QR Code Section
        sb.append("<tr><td style='padding:20px 40px;'>");
        sb.append(
                "<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background:linear-gradient(135deg,#1a1a2e,#0f3460);border-radius:12px;overflow:hidden;'>");
        sb.append("<tr><td style='padding:25px;text-align:center;'>");
        sb.append(
                "<p style='color:#aaa;font-size:12px;text-transform:uppercase;letter-spacing:2px;margin:0 0 8px;'>Mã Đặt Vé</p>");
        sb.append("<p style='color:#e94560;font-size:26px;font-weight:bold;letter-spacing:4px;margin:0 0 20px;'>")
                .append(esc(bookingCode)).append("</p>");
        // QR Code Image
        sb.append("<div style='background:#ffffff;display:inline-block;padding:12px;border-radius:8px;'>");
        sb.append("<img src='").append(qrBase64)
                .append("' alt='QR Code' width='180' height='180' style='display:block;'/>");
        sb.append("</div>");
        sb.append(
                "<p style='color:#ccc;font-size:11px;margin:12px 0 0;'>Đem mã Barcode/QR này đến quầy giao dịch hoặc nhân viên soát vé để nhận vé.</p>");
        sb.append("</td></tr>");
        sb.append("</table>");
        sb.append("</td></tr>");

        // Ticket Details Section
        sb.append("<tr><td style='padding:10px 40px 5px;'>");
        sb.append(
                "<h2 style='color:#1a1a2e;font-size:18px;margin:0 0 15px;border-bottom:2px solid #e94560;padding-bottom:8px;'>&#127903; Chi tiết vé của bạn</h2>");
        sb.append("<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='font-size:14px;'>");

        sb.append(buildDetailRow("Phim", movieTitle));
        sb.append(buildDetailRow("Thời gian chiếu", showtime));
        sb.append(buildDetailRow("Rạp chiếu", cinemaName));
        sb.append(buildDetailRow("Địa chỉ", cinemaAddress));
        sb.append(buildDetailRow("Phòng / Số lượng vé", roomName + " / " + ticketCount + " vé"));
        sb.append(buildDetailRow("Số ghế", seatCodes));
        sb.append(buildDetailRow("Thức ăn kèm", comboDetails));

        // Total price row - highlighted
        sb.append("<tr>");
        sb.append(
                "<td style='padding:12px 0;border-bottom:1px solid #eee;color:#555;font-weight:bold;width:40%;vertical-align:top;'>Tổng tiền</td>");
        sb.append(
                "<td style='padding:12px 0;border-bottom:1px solid #eee;color:#e94560;font-weight:bold;font-size:18px;'>")
                .append(esc(totalPrice)).append(" đ</td>");
        sb.append("</tr>");

        sb.append("</table>");
        sb.append("</td></tr>");

        // Customer Info Section
        sb.append("<tr><td style='padding:20px 40px 5px;'>");
        sb.append(
                "<h2 style='color:#1a1a2e;font-size:18px;margin:0 0 15px;border-bottom:2px solid #e94560;padding-bottom:8px;'>&#128100; Thông tin người đặt</h2>");
        sb.append("<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='font-size:14px;'>");
        sb.append(buildDetailRow("Họ và tên", customerName));
        sb.append(buildDetailRow("Số điện thoại", customerPhone));
        sb.append(buildDetailRow("Email", customerEmail));
        sb.append("</table>");
        sb.append("</td></tr>");

        // Refund Policy
        sb.append("<tr><td style='padding:20px 40px 5px;'>");
        sb.append(
                "<div style='background:#fff8f0;border-left:4px solid #ff9800;padding:15px 20px;border-radius:0 8px 8px 0;'>");
        sb.append("<h3 style='color:#e65100;font-size:14px;margin:0 0 8px;'>&#9888;&#65039; Chính sách hoàn/huỷ</h3>");
        sb.append(
                "<p style='font-size:13px;color:#666;margin:0;line-height:1.6;'>Hệ thống không hỗ trợ đổi trả đối với các vé xem phim đã mua thành công. Trường hợp giao dịch của bạn gặp sự cố hoặc đang chờ xử lý, vui lòng liên hệ với ban quản lý rạp để được hỗ trợ.</p>");
        sb.append("</div>");
        sb.append("</td></tr>");

        // Note
        sb.append("<tr><td style='padding:15px 40px 5px;'>");
        sb.append(
                "<div style='background:#f0f7ff;border-left:4px solid #2196f3;padding:15px 20px;border-radius:0 8px 8px 0;'>");
        sb.append("<h3 style='color:#1565c0;font-size:14px;margin:0 0 8px;'>&#128221; Lưu ý</h3>");
        sb.append(
                "<p style='font-size:13px;color:#666;margin:0;line-height:1.6;'>Khi được yêu cầu, vui lòng xuất trình giấy tờ tùy thân để chứng thực độ tuổi (đối với các phim có giới hạn độ tuổi).</p>");
        sb.append("</div>");
        sb.append("</td></tr>");

        // Contact Support
        sb.append("<tr><td style='padding:15px 40px 25px;'>");
        sb.append(
                "<div style='background:#f0fff0;border-left:4px solid #4caf50;padding:15px 20px;border-radius:0 8px 8px 0;'>");
        sb.append("<h3 style='color:#2e7d32;font-size:14px;margin:0 0 8px;'>&#128222; Liên hệ hỗ trợ</h3>");
        sb.append(
                "<p style='font-size:13px;color:#666;margin:0;line-height:1.6;'>Vui lòng liên hệ trực tiếp với tổng đài CSKH tại số <strong>1900 123 456</strong> hoặc gửi email về <strong>support@cinemasystem.vn</strong> để được hỗ trợ kịp thời.</p>");
        sb.append("</div>");
        sb.append("</td></tr>");

        // Footer
        sb.append("<tr><td style='background:#1a1a2e;padding:20px 40px;text-align:center;'>");
        sb.append("<p style='color:#888;font-size:12px;margin:0;'>© 2026 Beta Two Cinema. All rights reserved.</p>");
        sb.append(
                "<p style='color:#666;font-size:11px;margin:5px 0 0;'>Email này được gửi tự động, vui lòng không trả lời.</p>");
        sb.append("</td></tr>");

        sb.append("</table>");
        sb.append("</td></tr>");
        sb.append("</table>");

        sb.append("</body></html>");
        return sb.toString();
    }

    private String buildDetailRow(String label, String value) {
        return "<tr>" +
                "<td style='padding:10px 0;border-bottom:1px solid #eee;color:#555;font-weight:bold;width:40%;vertical-align:top;'>"
                + esc(label) + "</td>" +
                "<td style='padding:10px 0;border-bottom:1px solid #eee;color:#333;'>" + esc(value) + "</td>" +
                "</tr>";
    }

    /**
     * Simple HTML escape
     */
    private String esc(String s) {
        if (s == null)
            return "N/A";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}