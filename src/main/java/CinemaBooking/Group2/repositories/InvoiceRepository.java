package CinemaBooking.Group2.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.invoice.InvoiceListResponse;

@Repository
public class InvoiceRepository {

    @Autowired
    private JdbcTemplate jdbc;

    /**
     * Lấy chi tiết hoá đơn theo booking_code
     */
    public Map<String, Object> findInvoiceByBookingCode(String bookingCode) {
        String sql = """
            SELECT
                b.id                AS bookingId,
                b.booking_code      AS bookingCode,
                b.payment_status    AS paymentStatus,
                b.payment_method    AS paymentMethod,
                b.total_price       AS totalPrice,
                b.discount_amount   AS discountAmount,
                b.created_at        AS createdAt,
                b.paid_at           AS paidAt,
                b.user_id           AS customerId,
                u.full_name         AS customerName,
                u.email             AS customerEmail,
                u.phone             AS customerPhone,
                m.title             AS movieTitle,
                m.poster_url        AS posterUrl,
                m.format            AS format,
                m.duration_minutes  AS durationMinutes,
                c.name              AS cinemaName,
                c.address           AS cinemaAddress,
                r.name              AS roomName,
                st.start_time       AS startTime,
                st.end_time         AS endTime,
                v.code              AS voucherCode
            FROM booking b
            JOIN showtime st  ON st.id  = b.showtime_id
            JOIN movie m      ON m.id   = st.movie_id
            JOIN room r       ON r.id   = st.room_id
            JOIN cinema c     ON c.id   = r.cinema_id
            LEFT JOIN `user` u ON u.id  = b.user_id
            LEFT JOIN voucher v ON v.id = b.voucher_id
            WHERE b.booking_code = ?
        """;
        try {
            return jdbc.queryForMap(sql, bookingCode);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Lấy danh sách ghế & trạng thái in vé của booking
     */
    public List<Map<String, Object>> findSeatsByBookingId(int bookingId) {
        String sql = """
            SELECT
                s.seat_code,
                s.seat_type,
                bs.seat_price,
                bs.ticket_code,
                bs.is_printed,
                bs.printed_at,
                u.full_name AS printed_by_name
            FROM booking_seat bs
            JOIN seat s ON s.id = bs.seat_id
            LEFT JOIN `user` u ON u.id = bs.printed_by
            WHERE bs.booking_id = ?
            ORDER BY s.seat_code
        """;
        try {
            return jdbc.queryForList(sql, bookingId);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Lấy danh sách combo của booking
     */
    public List<Map<String, Object>> findCombosByBookingId(int bookingId) {
        String sql = """
            SELECT cb.name, bc.quantity, bc.price
            FROM booking_concession bc
            LEFT JOIN combo cb ON cb.id = bc.combo_id
            WHERE bc.booking_id = ?
        """;
        try {
            return jdbc.queryForList(sql, bookingId);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Tìm kiếm / lọc danh sách hoá đơn (Admin)
     */
    public long countInvoices(String keyword, String status, String startDate, String endDate, Integer cinemaId) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT b.id)
            FROM booking b
            JOIN showtime st  ON st.id = b.showtime_id
            JOIN movie m      ON m.id  = st.movie_id
            JOIN room r       ON r.id  = st.room_id
            JOIN cinema c     ON c.id  = r.cinema_id
            LEFT JOIN `user` u ON u.id = b.user_id
        """);

        List<Object> params = buildWhereClause(sql, keyword, status, startDate, endDate, cinemaId);

        try {
            Long result = jdbc.queryForObject(sql.toString(), Long.class, params.toArray());
            return result != null ? result : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<InvoiceListResponse> searchInvoices(
            String keyword, String status, String startDate, String endDate,
            Integer cinemaId, int page, int perPage) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                b.id                AS bookingId,
                b.booking_code      AS bookingCode,
                b.payment_status    AS paymentStatus,
                b.payment_method    AS paymentMethod,
                b.total_price       AS totalPrice,
                b.created_at        AS createdAt,
                b.paid_at           AS paidAt,
                u.full_name         AS customerName,
                u.email             AS customerEmail,
                m.title             AS movieTitle,
                c.name              AS cinemaName,
                st.start_time       AS startTime,
                GROUP_CONCAT(DISTINCT s.seat_code ORDER BY s.seat_code SEPARATOR ', ') AS seatCodes,
                COUNT(DISTINCT bs.id) AS seatCount
            FROM booking b
            JOIN showtime st  ON st.id  = b.showtime_id
            JOIN movie m      ON m.id   = st.movie_id
            JOIN room r       ON r.id   = st.room_id
            JOIN cinema c     ON c.id   = r.cinema_id
            LEFT JOIN `user` u  ON u.id  = b.user_id
            LEFT JOIN booking_seat bs ON bs.booking_id = b.id
            LEFT JOIN seat s          ON s.id = bs.seat_id
        """);

        List<Object> params = buildWhereClause(sql, keyword, status, startDate, endDate, cinemaId);

        sql.append("""
             GROUP BY b.id, b.booking_code, b.payment_status, b.payment_method,
                      b.total_price, b.created_at, b.paid_at,
                      u.full_name, u.email, m.title, c.name, st.start_time
             ORDER BY b.created_at DESC
             LIMIT ? OFFSET ?
        """);

        int offset = (page - 1) * perPage;
        params.add(perPage);
        params.add(offset);

        try {
            return jdbc.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                InvoiceListResponse dto = new InvoiceListResponse();
                dto.setBookingId(rs.getInt("bookingId"));
                dto.setBookingCode(rs.getString("bookingCode"));
                dto.setInvoiceNumber("INV-" + rs.getString("bookingCode"));
                dto.setCustomerName(rs.getString("customerName"));
                dto.setCustomerEmail(rs.getString("customerEmail"));
                dto.setMovieTitle(rs.getString("movieTitle"));
                dto.setCinemaName(rs.getString("cinemaName"));
                dto.setTotalPrice(rs.getBigDecimal("totalPrice"));
                dto.setSeatCodes(rs.getString("seatCodes"));
                dto.setSeatCount(rs.getInt("seatCount"));

                java.sql.Timestamp createdAt = rs.getTimestamp("createdAt");
                if (createdAt != null) dto.setCreatedAt(createdAt.toLocalDateTime());

                java.sql.Timestamp paidAt = rs.getTimestamp("paidAt");
                if (paidAt != null) dto.setPaidAt(paidAt.toLocalDateTime());

                java.sql.Timestamp startTime = rs.getTimestamp("startTime");
                if (startTime != null) dto.setStartTime(startTime.toLocalDateTime());

                String payStatus = rs.getString("paymentStatus");
                dto.setPaymentStatus(payStatus);
                dto.setPaymentStatusLabel(mapStatusLabel(payStatus));

                String payMethod = rs.getString("paymentMethod");
                dto.setPaymentMethod(payMethod);
                dto.setPaymentMethodLabel(mapMethodLabel(payMethod));

                return dto;
            });
        } catch (Exception e) {
            return List.of();
        }
    }

    // ===== PRIVATE HELPERS =====

    private List<Object> buildWhereClause(StringBuilder sql, String keyword, String status,
                                           String startDate, String endDate, Integer cinemaId) {
        List<Object> params = new ArrayList<>();
        sql.append(" WHERE 1=1 ");

        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (b.booking_code LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (status != null && !status.isBlank()) {
            sql.append("AND b.payment_status = ? ");
            params.add(status.toUpperCase());
        }
        if (startDate != null && !startDate.isBlank()) {
            sql.append("AND DATE(b.created_at) >= ? ");
            params.add(startDate);
        }
        if (endDate != null && !endDate.isBlank()) {
            sql.append("AND DATE(b.created_at) <= ? ");
            params.add(endDate);
        }
        if (cinemaId != null && cinemaId > 0) {
            sql.append("AND c.id = ? ");
            params.add(cinemaId);
        }

        return params;
    }

    public static String mapStatusLabel(String status) {
        if (status == null) return "";
        return switch (status) {
            case "PAID"      -> "Đã thanh toán";
            case "PENDING"   -> "Chờ thanh toán";
            case "FAILED"    -> "Thất bại";
            case "CANCELLED" -> "Đã huỷ";
            default          -> status;
        };
    }

    public static String mapMethodLabel(String method) {
        if (method == null) return "";
        return switch (method) {
            case "CASH"    -> "Tiền mặt";
            case "MOMO"    -> "Ví MoMo";
            case "ZALOPAY" -> "ZaloPay";
            case "VNPAY"   -> "VNPay";
            case "STRIPE"  -> "Stripe";
            default        -> method;
        };
    }
}