package CinemaBooking.Group2.repositories;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.booking.BookingDetailResponse;
import CinemaBooking.Group2.dtos.booking.BookingHistoryResponse;
import CinemaBooking.Group2.models.Booking;
import CinemaBooking.Group2.models.BookingConcession;
import CinemaBooking.Group2.models.BookingSeat;
import CinemaBooking.Group2.models.PriceAdjustment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class BookingRepository {

    private static final Logger logger = LoggerFactory.getLogger(BookingRepository.class);

    @Autowired
    private JdbcTemplate jdbc;

    /**
     * Custom RowMapper for Booking that properly handles enum fields (paymentStatus, paymentMethod)
     */
    private final RowMapper<Booking> bookingRowMapper = (ResultSet rs, int rowNum) -> {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setBookingCode(rs.getString("bookingCode"));
        
        // userId can be null
        int userId = rs.getInt("userId");
        b.setUserId(rs.wasNull() ? null : userId);
        
        b.setCreatedByStaffId(rs.getInt("createdByStaffId"));
        b.setShowtimeId(rs.getInt("showtimeId"));
        b.setVoucherId(rs.getInt("voucherId"));
        b.setDiscountAmount(rs.getBigDecimal("discountAmount"));
        b.setTotalPrice(rs.getBigDecimal("totalPrice"));
        
        // Safely convert String to enum for paymentStatus
        String statusStr = rs.getString("paymentStatus");
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                b.setPaymentStatus(Booking.PaymentStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown paymentStatus '{}' for booking {}", statusStr, rs.getInt("id"));
            }
        }
        
        // Safely convert String to enum for paymentMethod
        String methodStr = rs.getString("paymentMethod");
        if (methodStr != null && !methodStr.isBlank()) {
            try {
                b.setPaymentMethod(Booking.PaymentMethod.valueOf(methodStr));
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown paymentMethod '{}' for booking {}", methodStr, rs.getInt("id"));
            }
        }
        
        // Handle LocalDateTime fields
        java.sql.Timestamp createdAt = rs.getTimestamp("createdAt");
        b.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        
        java.sql.Timestamp paidAt = rs.getTimestamp("paidAt");
        b.setPaidAt(paidAt != null ? paidAt.toLocalDateTime() : null);
        
        return b;
    };

    private final RowMapper<PriceAdjustment> priceAdjustmentRowMapper = (ResultSet rs, int rowNum) -> {
        PriceAdjustment p = new PriceAdjustment();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        
        String typeStr = rs.getString("adjustment_type");
        if (typeStr != null) {
            try {
                p.setAdjustmentType(PriceAdjustment.AdjustmentType.valueOf(typeStr));
            } catch (Exception e) {
                // Ignore invalid enum
            }
        }
        
        p.setValue(rs.getBigDecimal("value"));
        p.setApplyOnDays(rs.getString("apply_on_days"));
        
        java.sql.Date start = rs.getDate("start_date");
        if (start != null) p.setStartDate(start.toLocalDate());
        
        java.sql.Date end = rs.getDate("end_date");
        if (end != null) p.setEndDate(end.toLocalDate());
        
        p.setIsActive(rs.getInt("is_active"));
        return p;
    };

    private static final String BOOKING_SELECT_COLUMNS = 
        "id, booking_code as bookingCode, user_id as userId, created_by_staff_id as createdByStaffId, " +
        "showtime_id as showtimeId, voucher_id as voucherId, discount_amount as discountAmount, " +
        "total_price as totalPrice, payment_status as paymentStatus, payment_method as paymentMethod, " +
        "created_at as createdAt, paid_at as paidAt";

    public Booking findById(int bookingId) {
        String sql = "SELECT " + BOOKING_SELECT_COLUMNS + " FROM booking WHERE id = ?";
        try {
            return jdbc.queryForObject(sql, bookingRowMapper, bookingId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            logger.error("Error finding booking by id {}: {}", bookingId, e.getMessage(), e);
            return null;
        }
    }

    public Booking findByBookingCode(String bookingCode) {
        String sql = "SELECT " + BOOKING_SELECT_COLUMNS + " FROM booking WHERE booking_code = ?";
        try {
            return jdbc.queryForObject(sql, bookingRowMapper, bookingCode);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            logger.error("Error finding booking by code {}: {}", bookingCode, e.getMessage(), e);
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
            Integer userId = booking.getUserId();
            if (userId != null) {
                ps.setInt(2, userId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
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
            WHERE is_active = 1 
        """;
        try {
            List<PriceAdjustment> list = jdbc.query(sql, priceAdjustmentRowMapper);
            logger.info("Found {} active price adjustments", list.size());
            return list;
        } catch (Exception e) {
            logger.error("Failed to fetch price adjustments", e);
            return List.of();
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
        String sql = """
            SELECT id, booking_id, seat_id, seat_price, ticket_code,
                   is_printed, printed_by, printed_at, printed_stamp
            FROM booking_seat WHERE booking_id = ?
        """;
        try {
            return jdbc.query(sql, (rs, rowNum) -> {
                BookingSeat bs = new BookingSeat();
                bs.setId(rs.getInt("id"));
                bs.setBookingId(rs.getInt("booking_id"));
                bs.setSeatId(rs.getInt("seat_id"));
                bs.setSeatPrice(rs.getBigDecimal("seat_price"));
                bs.setTicketCode(rs.getString("ticket_code"));
                
                Object isPrintedObj = rs.getObject("is_printed");
                if (isPrintedObj != null) {
                    if (isPrintedObj instanceof Boolean) {
                        bs.setIsPrinted((Boolean) isPrintedObj);
                    } else if (isPrintedObj instanceof Number) {
                        bs.setIsPrinted(((Number) isPrintedObj).intValue() == 1);
                    }
                } else {
                    bs.setIsPrinted(false);
                }
                
                bs.setPrintedBy(rs.getInt("printed_by"));
                
                java.sql.Timestamp printedAt = rs.getTimestamp("printed_at");
                bs.setPrintedAt(printedAt != null ? printedAt.toLocalDateTime() : null);
                
                bs.setPrintedStamp(rs.getString("printed_stamp"));
                return bs;
            }, bookingId);
        } catch (Exception e) {
            logger.error("Error finding booking seats for booking {}: {}", bookingId, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Lấy danh sách seat_code của booking bằng 1 query JOIN duy nhất.
     * Tránh vấn đề mapping enum SeatType khi dùng SeatRepository.findById().
     */
    public List<String> findSeatCodesByBookingId(int bookingId) {
        String sql = """
            SELECT s.seat_code
            FROM booking_seat bs
            JOIN seat s ON s.id = bs.seat_id
            WHERE bs.booking_id = ?
            ORDER BY s.seat_code
        """;
        try {
            return jdbc.queryForList(sql, String.class, bookingId);
        } catch (Exception e) {
            logger.error("Error finding seat codes for booking {}: {}", bookingId, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Lấy chi tiết combo/product kèm tên bằng 1 query JOIN duy nhất.
     * Tránh vấn đề BeanPropertyRowMapper không map được NULL vào int primitive.
     */
    public List<java.util.Map<String, Object>> findConcessionDetailsByBookingId(int bookingId) {
        String sql = """
            SELECT bc.quantity, bc.price,
                   COALESCE(cb.name, p.name, 'Sản phẩm') AS item_name
            FROM booking_concession bc
            LEFT JOIN combo cb ON cb.id = bc.combo_id
            LEFT JOIN product p ON p.id = bc.product_id
            WHERE bc.booking_id = ?
        """;
        try {
            return jdbc.queryForList(sql, bookingId);
        } catch (Exception e) {
            logger.error("Error finding concession details for booking {}: {}", bookingId, e.getMessage(), e);
            return List.of();
        }
    }

    public List<BookingConcession> findBookingConcessions(int bookingId) {
        String sql = """
            SELECT id, booking_id AS bookingId, product_id AS productId, 
                   combo_id AS comboId, quantity, price
            FROM booking_concession WHERE booking_id = ?
        """;
        try {
            return jdbc.query(sql, new BeanPropertyRowMapper<>(BookingConcession.class), bookingId);
        } catch (Exception e) {
            logger.error("Error finding booking concessions for booking {}: {}", bookingId, e.getMessage(), e);
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
    
    public ApiResponse<BookingDetailResponse> getMyBookingByCode(int userId, String code) {
        
    	String sql = """
    	        SELECT
    	            b.id AS id,
    	            b.booking_code AS bookingCode,
    	            b.payment_status AS status,
    	            b.payment_method AS paymentMethod,
    	            b.created_at AS createdAt,
    	            b.paid_at AS paidAt,
    	            b.total_price AS totalPrice,
    	            
    	            (SELECT COUNT(*) FROM booking_seat bs WHERE bs.booking_id = b.id AND bs.is_printed = 1) > 0 AS isCheckin,

    	            m.title AS movieTitle,
    	            m.poster_url AS posterUrl,
    	            m.short_description AS tagline,
    	            m.format AS format,
    	            m.duration_minutes AS durationMinutes,
    	            c.name AS cinemaName,
    	            c.address AS cinemaAddress,
    	            r.name AS roomName,
    	            st.start_time AS startTime,
    	            st.end_time AS endTime
    	        FROM booking b
    	        JOIN showtime st ON st.id = b.showtime_id
    	        JOIN movie m ON m.id = st.movie_id
    	        JOIN room r ON r.id = st.room_id
    	        JOIN cinema c ON c.id = r.cinema_id
    	        WHERE b.booking_code = ? AND b.user_id = ?
    	    """;

        BookingDetailResponse dto;
        try {
            dto = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(BookingDetailResponse.class), code, userId);
        } catch (EmptyResultDataAccessException e) {
            return new ApiResponse<>("Booking not found", null);
        }

        int bookingId = dto.getId();

        dto.setQrData(dto.getBookingCode());
        String status = dto.getStatus();
        if (status == null) status = "";
        
        switch (status) {
            case "PAID": dto.setStatusLabel("Đã thanh toán"); break;
            case "PENDING": dto.setStatusLabel("Chờ thanh toán"); break;
            case "FAILED": dto.setStatusLabel("Thất bại"); break;
            case "CANCELLED": dto.setStatusLabel("Đã hủy"); break;
            default: dto.setStatusLabel(status); break;
        }

        // Query tickets với thông tin chi tiết từng vé
        String ticketSql = """
            SELECT s.seat_code, s.seat_type, bs.seat_price, bs.ticket_code, bs.is_printed 
            FROM booking_seat bs 
            JOIN seat s ON s.id = bs.seat_id 
            WHERE bs.booking_id = ? 
            ORDER BY s.seat_code
        """;
        List<java.util.Map<String, Object>> ticketRows = jdbc.queryForList(ticketSql, bookingId);
        
        StringBuilder seatCodes = new StringBuilder();
        int seatCount = 0;
        java.math.BigDecimal seatsTotal = java.math.BigDecimal.ZERO;
        java.util.List<BookingDetailResponse.TicketInfo> tickets = new java.util.ArrayList<>();
        
        for (java.util.Map<String, Object> r : ticketRows) {
            if (seatCount > 0) seatCodes.append(", ");
            String seatCode = r.get("seat_code") != null ? r.get("seat_code").toString() : "";
            seatCodes.append(seatCode);
            seatCount++;
            
            // Lấy giá vé
            java.math.BigDecimal seatPrice = java.math.BigDecimal.ZERO;
            Object priceObj = r.get("seat_price");
            if (priceObj instanceof java.math.BigDecimal) {
                seatPrice = (java.math.BigDecimal) priceObj;
            } else if (priceObj != null) {
                seatPrice = new java.math.BigDecimal(priceObj.toString());
            }
            seatsTotal = seatsTotal.add(seatPrice);
            
            // Tạo TicketInfo
            String seatType = r.get("seat_type") != null ? r.get("seat_type").toString() : "STANDARD";
            String ticketCode = r.get("ticket_code") != null ? r.get("ticket_code").toString() : "";
            boolean isPrinted = false;
            Object isPrintedObj = r.get("is_printed");
            if (isPrintedObj != null) {
                if (isPrintedObj instanceof Boolean) {
                    isPrinted = (Boolean) isPrintedObj;
                } else if (isPrintedObj instanceof Number) {
                    isPrinted = ((Number) isPrintedObj).intValue() == 1;
                }
            }
            
            tickets.add(new BookingDetailResponse.TicketInfo(seatCode, seatType, seatPrice, ticketCode, isPrinted));
        }
        
        dto.setSeatCodes(seatCodes.toString());
        dto.setTickets(tickets);

        java.util.List<BookingDetailResponse.BillItem> items = new java.util.ArrayList<>();
        if (seatCount > 0) {
            items.add(new BookingDetailResponse.BillItem(
                "Vé (" + seatCodes.toString() + ")", seatCount, seatsTotal
            ));
        }

        // 4. Combos: Dùng bookingId
        String comboSql = "SELECT bc.quantity, bc.price, cb.name FROM booking_concession bc LEFT JOIN combo cb ON cb.id = bc.combo_id WHERE bc.booking_id = ?";
        List<java.util.Map<String, Object>> comboRows = jdbc.queryForList(comboSql, bookingId);
        
        for (java.util.Map<String, Object> r : comboRows) {
            Object nameObj = r.get("name");
            String name = nameObj != null ? nameObj.toString() : "Combo";
            
            int qty = 0;
            Object qtyObj = r.get("quantity");
            if (qtyObj != null) qty = Integer.parseInt(qtyObj.toString());
            
            java.math.BigDecimal price = java.math.BigDecimal.ZERO;
            Object priceObj = r.get("price");
            if (priceObj instanceof java.math.BigDecimal) {
                price = (java.math.BigDecimal) priceObj;
            } else if (priceObj != null) {
                price = new java.math.BigDecimal(priceObj.toString());
            }
            
            items.add(new BookingDetailResponse.BillItem(name, qty, price));
        }

        dto.setItems(items);
        dto.setShowDate(dto.getStartTime());

        return new ApiResponse<>("Success", dto);
    }
    
    public ApiResponse<BookingDetailResponse> getBookingByCodeAdmin(String code) {
        
        String sql = """
                SELECT
                    b.id AS id,
                    b.booking_code AS bookingCode,
                    b.payment_status AS status,
                    b.payment_method AS paymentMethod,
                    b.created_at AS createdAt,
                    b.paid_at AS paidAt,
                    b.total_price AS totalPrice,
                    b.user_id AS userId,
                    
                    (SELECT COUNT(*) FROM booking_seat bs WHERE bs.booking_id = b.id AND bs.is_printed = 1) > 0 AS isCheckin,

                    m.title AS movieTitle,
                    m.poster_url AS posterUrl,
                    m.short_description AS tagline,
                    m.format AS format,
                    m.duration_minutes AS durationMinutes,
                    c.name AS cinemaName,
                    c.address AS cinemaAddress,
                    r.name AS roomName,
                    st.start_time AS startTime,
                    st.end_time AS endTime
                FROM booking b
                JOIN showtime st ON st.id = b.showtime_id
                JOIN movie m ON m.id = st.movie_id
                JOIN room r ON r.id = st.room_id
                JOIN cinema c ON c.id = r.cinema_id
                WHERE b.booking_code = ?
            """;

        BookingDetailResponse dto;
        try {
            dto = jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(BookingDetailResponse.class), code);
        } catch (EmptyResultDataAccessException e) {
            return new ApiResponse<>("Booking not found", null);
        }

        int bookingId = dto.getId();

        dto.setQrData(dto.getBookingCode());
        String status = dto.getStatus();
        if (status == null) status = "";
        
        switch (status) {
            case "PAID": dto.setStatusLabel("Đã thanh toán"); break;
            case "PENDING": dto.setStatusLabel("Chờ thanh toán"); break;
            case "FAILED": dto.setStatusLabel("Thất bại"); break;
            default: dto.setStatusLabel(status); break;
        }

        // Query tickets với thông tin chi tiết từng vé
        String ticketSql = """
            SELECT s.seat_code, s.seat_type, bs.seat_price, bs.ticket_code, bs.is_printed 
            FROM booking_seat bs 
            JOIN seat s ON s.id = bs.seat_id 
            WHERE bs.booking_id = ? 
            ORDER BY s.seat_code
        """;
        List<java.util.Map<String, Object>> ticketRows = jdbc.queryForList(ticketSql, bookingId);
        
        StringBuilder seatCodes = new StringBuilder();
        int seatCount = 0;
        java.math.BigDecimal seatsTotal = java.math.BigDecimal.ZERO;
        java.util.List<BookingDetailResponse.TicketInfo> tickets = new java.util.ArrayList<>();
        
        for (java.util.Map<String, Object> r : ticketRows) {
            if (seatCount > 0) seatCodes.append(", ");
            String seatCode = r.get("seat_code") != null ? r.get("seat_code").toString() : "";
            seatCodes.append(seatCode);
            seatCount++;
            
            // Lấy giá vé
            java.math.BigDecimal seatPrice = java.math.BigDecimal.ZERO;
            Object priceObj = r.get("seat_price");
            if (priceObj instanceof java.math.BigDecimal) {
                seatPrice = (java.math.BigDecimal) priceObj;
            } else if (priceObj != null) {
                seatPrice = new java.math.BigDecimal(priceObj.toString());
            }
            seatsTotal = seatsTotal.add(seatPrice);
            
            // Tạo TicketInfo
            String seatType = r.get("seat_type") != null ? r.get("seat_type").toString() : "STANDARD";
            String ticketCode = r.get("ticket_code") != null ? r.get("ticket_code").toString() : "";
            boolean isPrinted = false;
            Object isPrintedObj = r.get("is_printed");
            if (isPrintedObj != null) {
                if (isPrintedObj instanceof Boolean) {
                    isPrinted = (Boolean) isPrintedObj;
                } else if (isPrintedObj instanceof Number) {
                    isPrinted = ((Number) isPrintedObj).intValue() == 1;
                }
            }
            
            tickets.add(new BookingDetailResponse.TicketInfo(seatCode, seatType, seatPrice, ticketCode, isPrinted));
        }
        
        dto.setSeatCodes(seatCodes.toString());
        dto.setTickets(tickets);

        java.util.List<BookingDetailResponse.BillItem> items = new java.util.ArrayList<>();
        if (seatCount > 0) {
            items.add(new BookingDetailResponse.BillItem(
                "Vé (" + seatCodes.toString() + ")", seatCount, seatsTotal
            ));
        }

        // Combos
        String comboSql = "SELECT bc.quantity, bc.price, cb.name FROM booking_concession bc LEFT JOIN combo cb ON cb.id = bc.combo_id WHERE bc.booking_id = ?";
        List<java.util.Map<String, Object>> comboRows = jdbc.queryForList(comboSql, bookingId);
        
        for (java.util.Map<String, Object> r : comboRows) {
            Object nameObj = r.get("name");
            String name = nameObj != null ? nameObj.toString() : "Combo";
            
            int qty = 0;
            Object qtyObj = r.get("quantity");
            if (qtyObj != null) qty = Integer.parseInt(qtyObj.toString());
            
            java.math.BigDecimal price = java.math.BigDecimal.ZERO;
            Object priceObj = r.get("price");
            if (priceObj instanceof java.math.BigDecimal) {
                price = (java.math.BigDecimal) priceObj;
            } else if (priceObj != null) {
                price = new java.math.BigDecimal(priceObj.toString());
            }
            
            items.add(new BookingDetailResponse.BillItem(name, qty, price));
        }

        dto.setItems(items);
        dto.setShowDate(dto.getStartTime());

        return new ApiResponse<>("Success", dto);
    }

    /**
     * Lấy danh sách vé của booking theo booking code (dùng cho in vé)
     */
    public List<java.util.Map<String, Object>> getTicketsForPrint(String bookingCode, List<String> ticketCodes) {
        StringBuilder sql = new StringBuilder("""
            SELECT bs.id, bs.ticket_code, bs.seat_price, bs.is_printed,
                   s.seat_code, s.seat_type
            FROM booking_seat bs
            JOIN booking b ON bs.booking_id = b.id
            JOIN seat s ON bs.seat_id = s.id
            WHERE b.booking_code = ?
        """);
        
        List<Object> params = new ArrayList<>();
        params.add(bookingCode);
        
        if (ticketCodes != null && !ticketCodes.isEmpty()) {
            sql.append(" AND bs.ticket_code IN (");
            for (int i = 0; i < ticketCodes.size(); i++) {
                sql.append(i == 0 ? "?" : ", ?");
                params.add(ticketCodes.get(i));
            }
            sql.append(")");
        }
        
        sql.append(" ORDER BY s.seat_code");
        
        try {
            return jdbc.queryForList(sql.toString(), params.toArray());
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Cập nhật trạng thái in vé
     */
    public int updateTicketPrintStatus(List<Integer> bookingSeatIds, int staffId, String printStamp) {
        if (bookingSeatIds == null || bookingSeatIds.isEmpty()) {
            return 0;
        }
        
        StringBuilder sql = new StringBuilder("""
            UPDATE booking_seat 
            SET is_printed = 1, printed_by = ?, printed_at = NOW(), printed_stamp = ?
            WHERE id IN (
        """);
        
        List<Object> params = new ArrayList<>();
        params.add(staffId);
        params.add(printStamp);
        
        for (int i = 0; i < bookingSeatIds.size(); i++) {
            sql.append(i == 0 ? "?" : ", ?");
            params.add(bookingSeatIds.get(i));
        }
        sql.append(") AND (is_printed = 0 OR is_printed IS NULL)");
        
        return jdbc.update(sql.toString(), params.toArray());
    }

    /**
     * Lấy thông tin booking cho in vé (bao gồm thông tin phim, rạp, suất chiếu)
     */
    public java.util.Map<String, Object> getBookingInfoForPrint(String bookingCode) {
        String sql = """
            SELECT 
                b.id AS bookingId,
                b.booking_code AS bookingCode,
                b.payment_status AS paymentStatus,
                m.title AS movieTitle,
                c.name AS cinemaName,
                r.name AS roomName,
                st.start_time AS startTime
            FROM booking b
            JOIN showtime st ON st.id = b.showtime_id
            JOIN movie m ON m.id = st.movie_id
            JOIN room r ON r.id = st.room_id
            JOIN cinema c ON c.id = r.cinema_id
            WHERE b.booking_code = ?
        """;
        
        try {
            return jdbc.queryForMap(sql, bookingCode);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Tự động hủy các booking PENDING quá hạn (quá X phút chưa thanh toán)
     * Trả về số lượng booking đã bị hủy
     */
    public int cancelExpiredPendingBookings(int timeoutMinutes) {
        // 1. Tìm các booking PENDING quá hạn
        String findExpiredSql = """
            SELECT id, booking_code 
            FROM booking 
            WHERE payment_status = 'PENDING' 
            AND created_at < DATE_SUB(NOW(), INTERVAL ? MINUTE)
        """;
        
        List<java.util.Map<String, Object>> expiredBookings;
        try {
            expiredBookings = jdbc.queryForList(findExpiredSql, timeoutMinutes);
        } catch (Exception e) {
            logger.error("Error finding expired bookings: {}", e.getMessage(), e);
            return 0;
        }
        
        if (expiredBookings.isEmpty()) {
            return 0;
        }
        
        int canceledCount = 0;
        
        // 2. Xóa từng booking (cascade delete sẽ xóa booking_seat và booking_concession)
        for (java.util.Map<String, Object> booking : expiredBookings) {
            try {
                int bookingId = ((Number) booking.get("id")).intValue();
                String bookingCode = (String) booking.get("booking_code");
                
                // Release any active seat holds related to this booking so seats become available again.
                // We DO NOT delete booking_seat or booking_concession rows because they are part of the
                // historical invoice record. Only update booking.payment_status to CANCELLED.
                try {
                    // Delete seat_hold entries that match seats from this booking for the same showtime
                    String deleteSeatHoldSql = "DELETE sh FROM seat_hold sh JOIN booking_seat bs ON sh.seat_id = bs.seat_id WHERE sh.showtime_id = ? AND bs.booking_id = ?";
                    // fetch showtime_id for this booking (safe fallback)
                    Integer showtimeId = jdbc.queryForObject("SELECT showtime_id FROM booking WHERE id = ?", Integer.class, bookingId);
                    if (showtimeId != null) {
                        jdbc.update(deleteSeatHoldSql, showtimeId, bookingId);
                    }
                } catch (Exception ex) {
                    logger.warn("Failed to release seat holds for expired booking {}: {}", bookingCode, ex.getMessage());
                }

                // Update booking status to CANCELLED (preserve booking detail rows)
                String updateBooking = "UPDATE booking SET payment_status = 'CANCELLED' WHERE id = ?";
                jdbc.update(updateBooking, bookingId);
                 
                 logger.info("Cancelled expired booking: {} (ID: {})", bookingCode, bookingId);
                 canceledCount++;
                 
             } catch (Exception e) {
                 logger.error("Error canceling booking {}: {}", booking.get("booking_code"), e.getMessage());
             }
         }
        
        return canceledCount;
    }
}
