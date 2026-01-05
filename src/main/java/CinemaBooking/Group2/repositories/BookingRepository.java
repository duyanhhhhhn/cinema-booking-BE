package CinemaBooking.Group2.repositories;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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
}
