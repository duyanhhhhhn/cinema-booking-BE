package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.invoice.InvoiceListResponse;
import CinemaBooking.Group2.dtos.invoice.InvoiceResponse;
import CinemaBooking.Group2.repositories.InvoiceRepository;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    // ─────────────────────────────────────────
    // Lấy chi tiết hoá đơn theo booking_code
    // ─────────────────────────────────────────
    public InvoiceResponse getInvoiceByBookingCode(String bookingCode) {
        Map<String, Object> row = invoiceRepository.findInvoiceByBookingCode(bookingCode);
        if (row == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found: " + bookingCode);
        }

        InvoiceResponse dto = new InvoiceResponse();

        // -- Thông tin booking --
        dto.setBookingId(toInt(row.get("bookingId")));
        dto.setBookingCode(str(row.get("bookingCode")));
        dto.setInvoiceNumber("INV-" + str(row.get("bookingCode")));
        dto.setCreatedAt(toLocalDateTime(row.get("createdAt")));
        dto.setPaidAt(toLocalDateTime(row.get("paidAt")));

        String payStatus = str(row.get("paymentStatus"));
        dto.setPaymentStatus(payStatus);
        dto.setPaymentStatusLabel(InvoiceRepository.mapStatusLabel(payStatus));

        String payMethod = str(row.get("paymentMethod"));
        dto.setPaymentMethod(payMethod);
        dto.setPaymentMethodLabel(InvoiceRepository.mapMethodLabel(payMethod));

        dto.setTotalPrice(toBigDecimal(row.get("totalPrice")));
        dto.setDiscountAmount(toBigDecimal(row.get("discountAmount")));
        dto.setVoucherCode(str(row.get("voucherCode")));

        // -- Thông tin khách hàng --
        Object customerIdObj = row.get("customerId");
        if (customerIdObj != null) dto.setCustomerId(toInt(customerIdObj));
        dto.setCustomerName(str(row.get("customerName")));
        dto.setCustomerEmail(str(row.get("customerEmail")));
        dto.setCustomerPhone(str(row.get("customerPhone")));

        // -- Thông tin phim / suất chiếu --
        dto.setMovieTitle(str(row.get("movieTitle")));
        dto.setPosterUrl(str(row.get("posterUrl")));
        dto.setFormat(str(row.get("format")));
        dto.setDurationMinutes(toInt(row.get("durationMinutes")));
        dto.setCinemaId(toInt(row.get("cinemaId")));
        dto.setCinemaName(str(row.get("cinemaName")));
        dto.setCinemaAddress(str(row.get("cinemaAddress")));
        dto.setRoomName(str(row.get("roomName")));
        dto.setStartTime(toLocalDateTime(row.get("startTime")));
        dto.setEndTime(toLocalDateTime(row.get("endTime")));

        int bookingId = dto.getBookingId();

        // -- Ghế --
        List<Map<String, Object>> seatRows = invoiceRepository.findSeatsByBookingId(bookingId);
        StringBuilder seatCodes = new StringBuilder();
        List<InvoiceResponse.InvoiceLineItem> lineItems = new ArrayList<>();
        List<InvoiceResponse.TicketPrintInfo> tickets = new ArrayList<>();

        BigDecimal seatsTotal = BigDecimal.ZERO;
        int seatCount = 0;

        for (Map<String, Object> sr : seatRows) {
            if (seatCount > 0) seatCodes.append(", ");
            seatCodes.append(str(sr.get("seat_code")));
            seatCount++;

            BigDecimal seatPrice = toBigDecimal(sr.get("seat_price"));
            seatsTotal = seatsTotal.add(seatPrice);

            // Build TicketPrintInfo cho từng ghế
            InvoiceResponse.TicketPrintInfo ticket = new InvoiceResponse.TicketPrintInfo();
            ticket.setSeatCode(str(sr.get("seat_code")));
            ticket.setSeatType(str(sr.get("seat_type")));
            ticket.setTicketCode(str(sr.get("ticket_code")));
            ticket.setSeatPrice(seatPrice);

            Object isPrintedObj = sr.get("is_printed");
            boolean isPrinted = isPrintedObj != null && (
                isPrintedObj instanceof Boolean ? (Boolean) isPrintedObj
                : "1".equals(isPrintedObj.toString()) || "true".equalsIgnoreCase(isPrintedObj.toString())
            );
            ticket.setPrinted(isPrinted);
            ticket.setPrintedByName(str(sr.get("printed_by_name")));
            ticket.setPrintedAt(toLocalDateTime(sr.get("printed_at")));

            tickets.add(ticket);
        }

        dto.setSeatCodes(seatCodes.toString());
        dto.setTickets(tickets);

        if (seatCount > 0) {
            lineItems.add(new InvoiceResponse.InvoiceLineItem(
                "Vé (" + seatCodes + ")",
                seatCount,
                seatCount > 0 ? seatsTotal.divide(BigDecimal.valueOf(seatCount), 0, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO,
                seatsTotal
            ));
        }

        // -- Combo --
        List<Map<String, Object>> comboRows = invoiceRepository.findCombosByBookingId(bookingId);
        for (Map<String, Object> cr : comboRows) {
            String name = str(cr.get("name"));
            int qty = toInt(cr.get("quantity"));
            BigDecimal price = toBigDecimal(cr.get("price"));
            BigDecimal unitPrice = qty > 0
                ? price.divide(BigDecimal.valueOf(qty), 0, java.math.RoundingMode.HALF_UP)
                : price;
            lineItems.add(new InvoiceResponse.InvoiceLineItem(name, qty, unitPrice, price));
        }

        dto.setLineItems(lineItems);

        return dto;
    }

    // ─────────────────────────────────────────
    // Lấy chi tiết hoá đơn theo booking_code (có kiểm tra quyền theo rạp)
    // ─────────────────────────────────────────
    public InvoiceResponse getInvoiceByBookingCodeWithAuth(String bookingCode, String role, Integer userCinemaId) {
        InvoiceResponse invoice = getInvoiceByBookingCode(bookingCode);

        // ADMIN xem được tất cả
        if ("ADMIN".equalsIgnoreCase(role)) {
            return invoice;
        }

        // MANAGER / STAFF chỉ xem được vé thuộc rạp của mình
        if (userCinemaId == null || !userCinemaId.equals(invoice.getCinemaId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vé không thuộc về rạp của bạn");
        }

        return invoice;
    }

    // ─────────────────────────────────────────
    // Danh sách hoá đơn có phân trang & lọc (Admin)
    // ─────────────────────────────────────────
    public PageResponse<InvoiceListResponse> searchInvoices(
            String keyword, String status, String startDate, String endDate,
            Integer cinemaId, int page, int perPage) {

        if (page < 1) page = 1;
        if (perPage < 1) perPage = 10;

        long total = invoiceRepository.countInvoices(keyword, status, startDate, endDate, cinemaId);
        List<InvoiceListResponse> items = invoiceRepository.searchInvoices(
                keyword, status, startDate, endDate, cinemaId, page, perPage);

        PageResponse.Meta meta = new PageResponse.Meta(total, perPage, page);
        return new PageResponse<>("Success", items, meta);
    }

    // ─────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────
    private String str(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private int toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try { return Integer.parseInt(obj.toString()); } catch (Exception e) { return 0; }
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        try { return new BigDecimal(obj.toString()); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    private java.time.LocalDateTime toLocalDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof java.sql.Timestamp) return ((java.sql.Timestamp) obj).toLocalDateTime();
        if (obj instanceof java.time.LocalDateTime) return (java.time.LocalDateTime) obj;
        return null;
    }
}