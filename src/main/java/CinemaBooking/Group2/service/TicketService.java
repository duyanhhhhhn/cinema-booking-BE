package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import CinemaBooking.Group2.dtos.ticket.PrintTicketRequest;
import CinemaBooking.Group2.dtos.ticket.PrintTicketResponse;
import CinemaBooking.Group2.repositories.BookingRepository;

@Service
public class TicketService {

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * In vé tại quầy
     * @param request Thông tin vé cần in
     * @param staffId ID của nhân viên thực hiện in vé
     * @return Thông tin vé đã in
     */
    @Transactional
    public PrintTicketResponse printTickets(PrintTicketRequest request, int staffId) {
        // 1. Validate booking code
        if (request.getBookingCode() == null || request.getBookingCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking code is required");
        }

        // 2. Lấy thông tin booking
        Map<String, Object> bookingInfo = bookingRepository.getBookingInfoForPrint(request.getBookingCode());
        if (bookingInfo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
        }

        // 3. Kiểm tra trạng thái thanh toán
        String paymentStatus = bookingInfo.get("paymentStatus") != null ? 
                bookingInfo.get("paymentStatus").toString() : "";
        if (!"PAID".equals(paymentStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot print tickets. Booking has not been paid yet. Current status: " + paymentStatus);
        }

        // 4. Lấy danh sách vé cần in
        List<Map<String, Object>> tickets = bookingRepository.getTicketsForPrint(
                request.getBookingCode(), 
                request.getTicketCodes()
        );

        if (tickets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tickets found for printing");
        }

        // 5. Kiểm tra và lọc vé chưa in
        List<Integer> ticketIdsToUpdate = new ArrayList<>();
        List<PrintTicketResponse.PrintedTicket> printedTickets = new ArrayList<>();
        List<String> alreadyPrintedTickets = new ArrayList<>();

        for (Map<String, Object> ticket : tickets) {
            boolean isPrinted = false;
            Object isPrintedObj = ticket.get("is_printed");
            if (isPrintedObj != null) {
                if (isPrintedObj instanceof Boolean) {
                    isPrinted = (Boolean) isPrintedObj;
                } else if (isPrintedObj instanceof Number) {
                    isPrinted = ((Number) isPrintedObj).intValue() == 1;
                }
            }

            String ticketCode = ticket.get("ticket_code") != null ? ticket.get("ticket_code").toString() : "";

            if (isPrinted) {
                alreadyPrintedTickets.add(ticketCode);
            } else {
                // Thêm vào danh sách cần cập nhật
                int ticketId = ticket.get("id") != null ? ((Number) ticket.get("id")).intValue() : 0;
                ticketIdsToUpdate.add(ticketId);

                // Tạo thông tin vé đã in
                String seatCode = ticket.get("seat_code") != null ? ticket.get("seat_code").toString() : "";
                String seatType = ticket.get("seat_type") != null ? ticket.get("seat_type").toString() : "STANDARD";
                
                BigDecimal price = BigDecimal.ZERO;
                Object priceObj = ticket.get("seat_price");
                if (priceObj instanceof BigDecimal) {
                    price = (BigDecimal) priceObj;
                } else if (priceObj != null) {
                    price = new BigDecimal(priceObj.toString());
                }

                // QR data chứa thông tin để quét vé
                String qrData = request.getBookingCode() + "|" + ticketCode + "|" + seatCode;

                printedTickets.add(new PrintTicketResponse.PrintedTicket(
                        ticketCode, seatCode, seatType, price, qrData
                ));
            }
        }

        // 6. Kiểm tra nếu tất cả vé đã được in
        if (ticketIdsToUpdate.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "All requested tickets have already been printed: " + String.join(", ", alreadyPrintedTickets));
        }

        // 7. Tạo print stamp (mã định danh lần in)
        String printStamp = generatePrintStamp();

        // 8. Cập nhật trạng thái in vé
        int updatedCount = bookingRepository.updateTicketPrintStatus(ticketIdsToUpdate, staffId, printStamp);

        if (updatedCount == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update ticket print status");
        }

        // 9. Build response
        PrintTicketResponse response = new PrintTicketResponse();
        response.setBookingCode(request.getBookingCode());
        response.setMovieTitle(bookingInfo.get("movieTitle") != null ? bookingInfo.get("movieTitle").toString() : "");
        response.setCinemaName(bookingInfo.get("cinemaName") != null ? bookingInfo.get("cinemaName").toString() : "");
        response.setRoomName(bookingInfo.get("roomName") != null ? bookingInfo.get("roomName").toString() : "");
        
        Object startTimeObj = bookingInfo.get("startTime");
        if (startTimeObj instanceof LocalDateTime) {
            response.setStartTime((LocalDateTime) startTimeObj);
        } else if (startTimeObj instanceof java.sql.Timestamp) {
            response.setStartTime(((java.sql.Timestamp) startTimeObj).toLocalDateTime());
        }

        response.setPrintedTickets(printedTickets);
        response.setTotalPrinted(printedTickets.size());
        response.setPrintedAt(LocalDateTime.now());
        response.setPrintStamp(printStamp);

        return response;
    }

    /**
     * Tạo mã stamp cho lần in vé (để tracking)
     */
    private String generatePrintStamp() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "PRT-" + timestamp + "-" + uuid;
    }
}
