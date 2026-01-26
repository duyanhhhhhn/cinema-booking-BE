package CinemaBooking.Group2.repositories;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.booking.BookingHistoryResponse;
import CinemaBooking.Group2.models.Booking;
import CinemaBooking.Group2.models.BookingConcession;
import CinemaBooking.Group2.models.BookingSeat;
import CinemaBooking.Group2.models.PriceAdjustment;

@Repository
public class BookingRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public Booking findById(int bookingId) {
        String sql = "SELECT * FROM booking WHERE id = ?";
        try {
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Booking.class), bookingId);
        } catch (Exception e) {
            return null;
        }
    }

    public Booking findByBookingCode(String bookingCode) {
        String sql = "SELECT * FROM booking WHERE booking_code = ?";
        try {
            return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Booking.class), bookingCode);
        } catch (Exception e) {
            return null;
        }
    }

    public int createBooking(Booking booking) {
        String sql = """
            INSERT INTO booking (booking_code, user_id, created_by_staff_id, showtime_id, 
                                voucher_id, discount_amount, total_price, payment_status, 
                                payment_method, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, booking.getBookingCode());
            ps.setInt(2, booking.getUserId());
            if (booking.getCreatedByStaffId() > 0) {
                ps.setInt(3, booking.getCreatedByStaffId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setInt(4, booking.getShowtimeId());
            if (booking.getVoucherId() > 0) {
                ps.setInt(5, booking.getVoucherId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setBigDecimal(6, booking.getDiscountAmount());
            ps.setBigDecimal(7, booking.getTotalPrice());
            ps.setString(8, booking.getPaymentStatus().name());
            ps.setString(9, booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null);
            return ps;
        }, keyHolder);
        
        return keyHolder.getKey().intValue();
    }

    public void createBookingSeat(BookingSeat bookingSeat) {
        String sql = """
            INSERT INTO booking_seat (booking_id, seat_id, seat_price, ticket_code)
            VALUES (?, ?, ?, ?)
        """;
        jdbc.update(sql, bookingSeat.getBookingId(), bookingSeat.getSeatId(), 
                   bookingSeat.getSeatPrice(), bookingSeat.getTicketCode());
    }

    public void createBookingConcession(BookingConcession concession) {
        String sql = """
            INSERT INTO booking_concession (booking_id, product_id, combo_id, quantity, price)
            VALUES (?, ?, ?, ?, ?)
        """;
        jdbc.update(sql, concession.getBookingId(), 
                   concession.getProductId() > 0 ? concession.getProductId() : null,
                   concession.getComboId() > 0 ? concession.getComboId() : null,
                   concession.getQuantity(), concession.getPrice());
    }

    public List<PriceAdjustment> findActivePriceAdjustments() {
        String sql = """
            SELECT * FROM price_adjustment 
            WHERE active = 1 
            AND (start_date IS NULL OR start_date <= CURDATE())
            AND (end_date IS NULL OR end_date >= CURDATE())
        """;
        try {
            return jdbc.query(sql, new BeanPropertyRowMapper<>(PriceAdjustment.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch price adjustments", e);
        }
    }

    public String generateBookingCode() {
        String sql = "SELECT CONCAT('BK', LPAD(IFNULL(MAX(id), 0) + 1, 8, '0')) as code FROM booking";
        try {
            return jdbc.queryForObject(sql, String.class);
        } catch (Exception e) {
            return "BK00000001";
        }
    }

    public boolean isSeatBooked(int seatId, int showtimeId) {
        String sql = """
            SELECT COUNT(*) FROM booking_seat bs
            JOIN booking b ON bs.booking_id = b.id
            WHERE bs.seat_id = ? AND b.showtime_id = ? 
            AND b.payment_status IN ('PENDING', 'PAID')
        """;
        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, seatId, showtimeId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<BookingSeat> findBookingSeats(int bookingId) {
        String sql = "SELECT * FROM booking_seat WHERE booking_id = ?";
        try {
            return jdbc.query(sql, new BeanPropertyRowMapper<>(BookingSeat.class), bookingId);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Create a payment record and return generated id
     */
    public int createPayment(CinemaBooking.Group2.models.Payment payment) {
        String sql = """
            INSERT INTO payment (booking_id, amount, method, provider_payment_id, status, paid_at, created_at)
            VALUES (?, ?, ?, ?, ?, ?, NOW())
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, payment.getBookingId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setString(3, payment.getMethod() != null ? payment.getMethod().name() : null);
            ps.setString(4, payment.getProviderPaymentId());
            ps.setString(5, payment.getStatus() != null ? payment.getStatus().name() : null);
            if (payment.getPaidAt() != null) {
                ps.setTimestamp(6, java.sql.Timestamp.valueOf(payment.getPaidAt()));
            } else {
                ps.setNull(6, java.sql.Types.TIMESTAMP);
            }
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void updateBookingPaymentStatus(int bookingId, String paymentStatus, LocalDateTime paidAt, String paymentMethod) {
        String sql = "UPDATE booking SET payment_status = ?, paid_at = ?, payment_method = ? WHERE id = ?";
        jdbc.update(sql, paymentStatus, paidAt != null ? java.sql.Timestamp.valueOf(paidAt) : null,
                    paymentMethod, bookingId);
    }

    public String getUserEmail(int userId) {
        try {
            String sql = "SELECT email FROM `user` WHERE id = ?";
            return jdbc.queryForObject(sql, String.class, userId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Find booking history by user ID (existing signature kept for compatibility)
     */
    public PageResponse<BookingHistoryResponse> searchBookings(
            int userId, String startDate, String endDate, 
            Booking.PaymentStatus status, String movieTitle, 
            int page, int perPage
    ) {
        // 1. Xây dựng WHERE và tham số
        StringBuilder whereSql = new StringBuilder("WHERE b.user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (startDate != null && !startDate.isBlank()) {
            whereSql.append("AND DATE(st.start_time) >= ? ");
            params.add(startDate);
        }
        if (endDate != null && !endDate.isBlank()) {
            whereSql.append("AND DATE(st.start_time) <= ? ");
            params.add(endDate);
        }
        if (status != null) {
            whereSql.append("AND b.payment_status = ? ");
            params.add(status.name());
        }
        if (movieTitle != null && !movieTitle.isBlank()) {
            whereSql.append("AND m.title LIKE ? ");
            params.add("%" + movieTitle + "%");
        }

        // 2. Query Đếm Tổng (Count)
        String countQuery = """
            SELECT COUNT(DISTINCT b.id) 
            FROM booking b
            JOIN showtime st ON b.showtime_id = st.id
            JOIN movie m ON st.movie_id = m.id
            JOIN room r ON st.room_id = r.id
            JOIN cinema c ON r.cinema_id = c.id
            """ + whereSql.toString();

        long total = 0;
        try {
            Long result = jdbc.queryForObject(countQuery, Long.class, params.toArray());
            total = result != null ? result : 0;
        } catch (Exception e) {
            total = 0;
        }

        // 3. Query Lấy Dữ Liệu (Select)
        StringBuilder selectSql = new StringBuilder("""
            SELECT 
                b.id, b.booking_code AS bookingCode,
                m.id AS movieId, m.title AS movieTitle, m.poster_url AS posterUrl,
                c.name AS cinemaName, r.name AS roomName,
                st.start_time AS startTime,
                GROUP_CONCAT(DISTINCT s.seat_code ORDER BY s.seat_code SEPARATOR ', ') AS seats,
                (
                    SELECT GROUP_CONCAT(CONCAT(cb.name, ' x', bc.quantity) SEPARATOR ', ')
                    FROM booking_concession bc
                    JOIN combo cb ON bc.combo_id = cb.id
                    WHERE bc.booking_id = b.id
                ) AS combos,
                b.total_price AS totalPrice,
                b.payment_status AS status,
                CASE 
                    WHEN b.payment_status = 'PAID' THEN 'Đã thanh toán'
                    WHEN b.payment_status = 'PENDING' THEN 'Chờ thanh toán'
                    WHEN b.payment_status = 'FAILED' THEN 'Thất bại'
                    ELSE b.payment_status
                END AS statusLabel
            FROM booking b
            JOIN showtime st ON b.showtime_id = st.id
            JOIN movie m ON st.movie_id = m.id
            JOIN room r ON st.room_id = r.id
            JOIN cinema c ON r.cinema_id = c.id
            LEFT JOIN booking_seat bs ON bs.booking_id = b.id
            LEFT JOIN seat s ON bs.seat_id = s.id
            """);

        selectSql.append(whereSql);
        
        selectSql.append("""
             GROUP BY b.id, b.booking_code, m.id, m.title, m.poster_url, 
                      c.name, r.name, st.start_time, b.total_price, b.payment_status
             ORDER BY b.created_at DESC
             LIMIT ? OFFSET ?
             """);

        int offset = (page - 1) * perPage;
        params.add(perPage);
        params.add(offset);

        List<BookingHistoryResponse> items;
        try {
            items = jdbc.query(selectSql.toString(), 
                     new BeanPropertyRowMapper<>(BookingHistoryResponse.class), 
                     params.toArray());
        } catch (Exception e) {
            items = List.of();
        }

        // 4. Đóng gói vào PageResponse ngay tại đây
        PageResponse.Meta meta = new PageResponse.Meta(total, perPage, page);
        return new PageResponse<>("Success", items, meta);
    }

}