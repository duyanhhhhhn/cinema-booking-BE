package CinemaBooking.Group2.repositories;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.AuditLog;

@Repository
public class DashboardRepository {

    private final DataSource dataSource;

    public DashboardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<AuditLog> findAllAuditLogs() {
        String sql = """
            SELECT id, user_id, action, resource_id, resource_type, details, ip_address, user_agent, created_at
            FROM audit_log
            ORDER BY created_at DESC, id DESC
            """;

        List<AuditLog> logs = new ArrayList<>();

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                logs.add(mapAuditLog(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch audit logs.", e);
        }

        return logs;
    }

    public boolean insertAuditLog(AuditLog item) {
        String sql = """
            INSERT INTO audit_log
            (user_id, action, resource_id, resource_type, details, ip_address, user_agent, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, item.getUserId());
            ps.setString(2, item.getAction());

            if (item.getResourceId() == null || item.getResourceId().isBlank()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, item.getResourceId());
            }

            ps.setString(4, item.getResourceType());
            ps.setString(5, item.getDetails());
            ps.setString(6, item.getIpAddress());
            ps.setString(7, item.getUserAgent());

            LocalDateTime createdAt = item.getCreatedAt() == null
                ? LocalDateTime.now()
                : item.getCreatedAt();

            ps.setTimestamp(8, Timestamp.valueOf(createdAt));

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to create audit log. userId=" + item.getUserId()
                    + ", action=" + item.getAction()
                    + ", resourceId=" + item.getResourceId()
                    + ", resourceType=" + item.getResourceType(),
                e
            );
        }
    }

    public Integer findCinemaIdByUserId(Integer userId) {
        String sql = """
            SELECT cinema_id
            FROM user
            WHERE id = ?
            LIMIT 1
            """;

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cinemaId = rs.getInt("cinema_id");
                    return rs.wasNull() ? null : cinemaId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to find cinema assignment by user id. userId=" + userId,
                e
            );
        }

        return null;
    }

    public Integer findCinemaIdByEmail(String email) {
        String sql = """
            SELECT cinema_id
            FROM user
            WHERE email = ?
            LIMIT 1
            """;

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cinemaId = rs.getInt("cinema_id");
                    return rs.wasNull() ? null : cinemaId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to find cinema assignment by email. email=" + email,
                e
            );
        }

        return null;
    }

    public RevenueSummaryRow findRevenueSummary(
        LocalDate startDate,
        LocalDate endDate,
        Integer cinemaId,
        Integer movieId
    ) {
        RevenueFilter paidFilter = buildRevenueFilter(startDate, endDate, cinemaId, movieId);

        String paidSql = """
            SELECT
                COALESCE(SUM(bs.seat_price), 0) AS total_revenue,
                COUNT(bs.id) AS total_tickets_sold,
                COUNT(DISTINCT b.id) AS total_paid_bookings
            FROM booking b
            JOIN booking_seat bs ON bs.booking_id = b.id
            JOIN showtime s ON s.id = b.showtime_id
            JOIN room r ON r.id = s.room_id
            JOIN cinema c ON c.id = r.cinema_id
            JOIN movie m ON m.id = s.movie_id
            WHERE b.payment_status = 'PAID'
              AND b.paid_at IS NOT NULL
              AND (s.status IS NULL OR s.status <> 'CANCELLED')
            """ + paidFilter.getSql();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalTicketsSold = 0;
        long totalPaidBookings = 0;

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(paidSql)
        ) {
            bindParams(ps, paidFilter.getParams());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalRevenue = safe(rs.getBigDecimal("total_revenue"));
                    totalTicketsSold = rs.getLong("total_tickets_sold");
                    totalPaidBookings = rs.getLong("total_paid_bookings");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to fetch revenue summary. startDate=" + startDate
                    + ", endDate=" + endDate
                    + ", cinemaId=" + cinemaId
                    + ", movieId=" + movieId,
                e
            );
        }

        long totalMovies = countMoviesInScope(cinemaId, movieId);
        long totalCinemas = countCinemasInScope(cinemaId, movieId);

        return new RevenueSummaryRow(
            totalRevenue,
            totalTicketsSold,
            totalPaidBookings,
            totalMovies,
            totalCinemas
        );
    }

    public List<DailyRevenueRow> findDailyRevenue(
        LocalDate startDate,
        LocalDate endDate,
        Integer cinemaId,
        Integer movieId
    ) {
        RevenueFilter filter = buildRevenueFilter(startDate, endDate, cinemaId, movieId);

        String sql = """
            SELECT
                DATE(b.paid_at) AS revenue_date,
                COALESCE(SUM(bs.seat_price), 0) AS total_revenue,
                COUNT(bs.id) AS total_tickets_sold,
                COUNT(DISTINCT b.id) AS total_paid_bookings
            FROM booking b
            JOIN booking_seat bs ON bs.booking_id = b.id
            JOIN showtime s ON s.id = b.showtime_id
            JOIN room r ON r.id = s.room_id
            JOIN cinema c ON c.id = r.cinema_id
            JOIN movie m ON m.id = s.movie_id
            WHERE b.payment_status = 'PAID'
              AND b.paid_at IS NOT NULL
              AND (s.status IS NULL OR s.status <> 'CANCELLED')
            """ + filter.getSql() + """
            GROUP BY DATE(b.paid_at)
            ORDER BY revenue_date ASC
            """;

        List<DailyRevenueRow> rows = new ArrayList<>();

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            bindParams(ps, filter.getParams());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date revenueDate = rs.getDate("revenue_date");

                    rows.add(new DailyRevenueRow(
                        revenueDate != null ? revenueDate.toLocalDate() : null,
                        safe(rs.getBigDecimal("total_revenue")),
                        rs.getLong("total_tickets_sold"),
                        rs.getLong("total_paid_bookings")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to fetch daily revenue. startDate=" + startDate
                    + ", endDate=" + endDate
                    + ", cinemaId=" + cinemaId
                    + ", movieId=" + movieId,
                e
            );
        }

        return rows;
    }

    public List<MovieRevenueRankingRow> findMovieRevenueRanking(
        LocalDate startDate,
        LocalDate endDate,
        Integer cinemaId,
        Integer movieId
    ) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                m.id AS movie_id,
                m.title AS movie_title,
                m.poster_url AS poster_url,
                COALESCE(SUM(bs.seat_price), 0) AS total_revenue,
                COUNT(bs.id) AS total_tickets_sold,
                COUNT(DISTINCT b.id) AS total_paid_bookings
            FROM movie m
            JOIN showtime s
                ON s.movie_id = m.id
               AND (s.status IS NULL OR s.status <> 'CANCELLED')
            JOIN room r
                ON r.id = s.room_id
            JOIN cinema c
                ON c.id = r.cinema_id
            LEFT JOIN booking b
                ON b.showtime_id = s.id
               AND b.payment_status = 'PAID'
               AND b.paid_at IS NOT NULL
            LEFT JOIN booking_seat bs
                ON bs.booking_id = b.id
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();

        if (startDate != null) {
            sql.append(" AND (b.id IS NULL OR DATE(b.paid_at) >= ?) ");
            params.add(startDate);
        }

        if (endDate != null) {
            sql.append(" AND (b.id IS NULL OR DATE(b.paid_at) <= ?) ");
            params.add(endDate);
        }

        if (cinemaId != null) {
            sql.append(" AND c.id = ? ");
            params.add(cinemaId);
        }

        if (movieId != null) {
            sql.append(" AND m.id = ? ");
            params.add(movieId);
        }

        sql.append("""
            GROUP BY m.id, m.title, m.poster_url
            ORDER BY total_revenue ASC, total_tickets_sold ASC, m.title ASC
            """);

        List<MovieRevenueRankingRow> rows = new ArrayList<>();

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {
            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new MovieRevenueRankingRow(
                        rs.getInt("movie_id"),
                        rs.getString("movie_title"),
                        rs.getString("poster_url"),
                        safe(rs.getBigDecimal("total_revenue")),
                        rs.getLong("total_tickets_sold"),
                        rs.getLong("total_paid_bookings")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to fetch movie revenue ranking. startDate=" + startDate
                    + ", endDate=" + endDate
                    + ", cinemaId=" + cinemaId
                    + ", movieId=" + movieId,
                e
            );
        }

        return rows;
    }

    public List<CinemaRevenueRankingRow> findCinemaRevenueRanking(
        LocalDate startDate,
        LocalDate endDate,
        Integer cinemaId,
        Integer movieId
    ) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                c.id AS cinema_id,
                c.name AS cinema_name,
                c.image_url AS cinema_image_url,
                COALESCE(SUM(bs.seat_price), 0) AS total_revenue,
                COUNT(bs.id) AS total_tickets_sold,
                COUNT(DISTINCT b.id) AS total_paid_bookings
            FROM cinema c
            LEFT JOIN room r
                ON r.cinema_id = c.id
            LEFT JOIN showtime s
                ON s.room_id = r.id
               AND (s.status IS NULL OR s.status <> 'CANCELLED')
            """);

        List<Object> params = new ArrayList<>();

        if (movieId != null) {
            sql.append(" AND s.movie_id = ? ");
            params.add(movieId);
        }

        sql.append("""
            LEFT JOIN booking b
                ON b.showtime_id = s.id
               AND b.payment_status = 'PAID'
               AND b.paid_at IS NOT NULL
            """);

        if (startDate != null) {
            sql.append(" AND DATE(b.paid_at) >= ? ");
            params.add(startDate);
        }

        if (endDate != null) {
            sql.append(" AND DATE(b.paid_at) <= ? ");
            params.add(endDate);
        }

        sql.append("""
            LEFT JOIN booking_seat bs
                ON bs.booking_id = b.id
            WHERE 1=1
            """);

        if (cinemaId != null) {
            sql.append(" AND c.id = ? ");
            params.add(cinemaId);
        }

        sql.append("""
            GROUP BY c.id, c.name, c.image_url
            ORDER BY total_revenue ASC, total_tickets_sold ASC, c.name ASC
            """);

        List<CinemaRevenueRankingRow> rows = new ArrayList<>();

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {
            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new CinemaRevenueRankingRow(
                        rs.getInt("cinema_id"),
                        rs.getString("cinema_name"),
                        rs.getString("cinema_image_url"),
                        safe(rs.getBigDecimal("total_revenue")),
                        rs.getLong("total_tickets_sold"),
                        rs.getLong("total_paid_bookings")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to fetch cinema revenue ranking. startDate=" + startDate
                    + ", endDate=" + endDate
                    + ", cinemaId=" + cinemaId
                    + ", movieId=" + movieId
                    + ", sql=" + sql,
                e
            );
        }

        return rows;
    }

    public List<MovieOptionRow> findReportMovieOptions(Integer cinemaId) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (cinemaId == null) {
            sql.append("""
                SELECT
                    m.id AS movie_id,
                    m.title AS movie_title,
                    m.poster_url AS poster_url
                FROM movie m
                ORDER BY m.title ASC
            """);
        } else {
            sql.append("""
                SELECT DISTINCT
                    m.id AS movie_id,
                    m.title AS movie_title,
                    m.poster_url AS poster_url
                FROM movie m
                JOIN showtime s
                    ON s.movie_id = m.id
                   AND (s.status IS NULL OR s.status <> 'CANCELLED')
                JOIN room r ON r.id = s.room_id
                WHERE r.cinema_id = ?
                ORDER BY m.title ASC
            """);
            params.add(cinemaId);
        }

        List<MovieOptionRow> rows = new ArrayList<>();

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {
            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new MovieOptionRow(
                        rs.getInt("movie_id"),
                        rs.getString("movie_title"),
                        rs.getString("poster_url")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to fetch report movie options. cinemaId=" + cinemaId,
                e
            );
        }

        return rows;
    }

    private long countMoviesInScope(Integer cinemaId, Integer movieId) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT m.id) AS total_movies
            FROM movie m
            JOIN showtime s
                ON s.movie_id = m.id
               AND (s.status IS NULL OR s.status <> 'CANCELLED')
            JOIN room r
                ON r.id = s.room_id
            JOIN cinema c
                ON c.id = r.cinema_id
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();

        if (cinemaId != null) {
            sql.append(" AND c.id = ? ");
            params.add(cinemaId);
        }

        if (movieId != null) {
            sql.append(" AND m.id = ? ");
            params.add(movieId);
        }

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {
            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total_movies");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to count movies in scope. cinemaId=" + cinemaId + ", movieId=" + movieId,
                e
            );
        }

        return 0;
    }

    private long countCinemasInScope(Integer cinemaId, Integer movieId) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT c.id) AS total_cinemas
            FROM cinema c
            JOIN room r
                ON r.cinema_id = c.id
            JOIN showtime s
                ON s.room_id = r.id
               AND (s.status IS NULL OR s.status <> 'CANCELLED')
            JOIN movie m
                ON m.id = s.movie_id
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();

        if (cinemaId != null) {
            sql.append(" AND c.id = ? ");
            params.add(cinemaId);
        }

        if (movieId != null) {
            sql.append(" AND m.id = ? ");
            params.add(movieId);
        }

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {
            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total_cinemas");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to count cinemas in scope. cinemaId=" + cinemaId + ", movieId=" + movieId,
                e
            );
        }

        return 0;
    }

    private RevenueFilter buildRevenueFilter(
        LocalDate startDate,
        LocalDate endDate,
        Integer cinemaId,
        Integer movieId
    ) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (startDate != null) {
            sql.append(" AND DATE(b.paid_at) >= ? ");
            params.add(startDate);
        }

        if (endDate != null) {
            sql.append(" AND DATE(b.paid_at) <= ? ");
            params.add(endDate);
        }

        if (cinemaId != null) {
            sql.append(" AND c.id = ? ");
            params.add(cinemaId);
        }

        if (movieId != null) {
            sql.append(" AND m.id = ? ");
            params.add(movieId);
        }

        return new RevenueFilter(sql.toString(), params);
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);

            if (value instanceof LocalDate localDate) {
                ps.setDate(i + 1, Date.valueOf(localDate));
            } else if (value instanceof Integer integer) {
                ps.setInt(i + 1, integer);
            } else if (value instanceof Long longValue) {
                ps.setLong(i + 1, longValue);
            } else if (value instanceof BigDecimal bigDecimal) {
                ps.setBigDecimal(i + 1, bigDecimal);
            } else if (value instanceof String string) {
                ps.setString(i + 1, string);
            } else {
                ps.setObject(i + 1, value);
            }
        }
    }

    private AuditLog mapAuditLog(ResultSet rs) throws SQLException {
        AuditLog item = new AuditLog();
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setAction(rs.getString("action"));
        item.setResourceId(rs.getString("resource_id"));
        item.setResourceType(rs.getString("resource_type"));
        item.setDetails(rs.getString("details"));
        item.setIpAddress(rs.getString("ip_address"));
        item.setUserAgent(rs.getString("user_agent"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            item.setCreatedAt(createdAt.toLocalDateTime());
        }

        return item;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static BigDecimal calcPercent(BigDecimal part, BigDecimal total) {
        if (part == null || total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return part.multiply(BigDecimal.valueOf(100))
            .divide(total, 2, RoundingMode.HALF_UP);
    }

    public static class RevenueSummaryRow {
        private final BigDecimal totalRevenue;
        private final long totalTicketsSold;
        private final long totalPaidBookings;
        private final long totalMovies;
        private final long totalCinemas;

        public RevenueSummaryRow(
            BigDecimal totalRevenue,
            long totalTicketsSold,
            long totalPaidBookings,
            long totalMovies,
            long totalCinemas
        ) {
            this.totalRevenue = totalRevenue;
            this.totalTicketsSold = totalTicketsSold;
            this.totalPaidBookings = totalPaidBookings;
            this.totalMovies = totalMovies;
            this.totalCinemas = totalCinemas;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public long getTotalTicketsSold() {
            return totalTicketsSold;
        }

        public long getTotalPaidBookings() {
            return totalPaidBookings;
        }

        public long getTotalMovies() {
            return totalMovies;
        }

        public long getTotalCinemas() {
            return totalCinemas;
        }
    }

    public static class DailyRevenueRow {
        private final LocalDate date;
        private final BigDecimal totalRevenue;
        private final long totalTicketsSold;
        private final long totalPaidBookings;

        public DailyRevenueRow(
            LocalDate date,
            BigDecimal totalRevenue,
            long totalTicketsSold,
            long totalPaidBookings
        ) {
            this.date = date;
            this.totalRevenue = totalRevenue;
            this.totalTicketsSold = totalTicketsSold;
            this.totalPaidBookings = totalPaidBookings;
        }

        public LocalDate getDate() {
            return date;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public long getTotalTicketsSold() {
            return totalTicketsSold;
        }

        public long getTotalPaidBookings() {
            return totalPaidBookings;
        }
    }

    public static class MovieRevenueRankingRow {
        private final Integer movieId;
        private final String movieTitle;
        private final String posterUrl;
        private final BigDecimal totalRevenue;
        private final long totalTicketsSold;
        private final long totalPaidBookings;

        public MovieRevenueRankingRow(
            Integer movieId,
            String movieTitle,
            String posterUrl,
            BigDecimal totalRevenue,
            long totalTicketsSold,
            long totalPaidBookings
        ) {
            this.movieId = movieId;
            this.movieTitle = movieTitle;
            this.posterUrl = posterUrl;
            this.totalRevenue = totalRevenue;
            this.totalTicketsSold = totalTicketsSold;
            this.totalPaidBookings = totalPaidBookings;
        }

        public Integer getMovieId() {
            return movieId;
        }

        public String getMovieTitle() {
            return movieTitle;
        }

        public String getPosterUrl() {
            return posterUrl;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public long getTotalTicketsSold() {
            return totalTicketsSold;
        }

        public long getTotalPaidBookings() {
            return totalPaidBookings;
        }
    }

    public static class CinemaRevenueRankingRow {
        private final Integer cinemaId;
        private final String cinemaName;
        private final String cinemaImageUrl;
        private final BigDecimal totalRevenue;
        private final long totalTicketsSold;
        private final long totalPaidBookings;

        public CinemaRevenueRankingRow(
            Integer cinemaId,
            String cinemaName,
            String cinemaImageUrl,
            BigDecimal totalRevenue,
            long totalTicketsSold,
            long totalPaidBookings
        ) {
            this.cinemaId = cinemaId;
            this.cinemaName = cinemaName;
            this.cinemaImageUrl = cinemaImageUrl;
            this.totalRevenue = totalRevenue;
            this.totalTicketsSold = totalTicketsSold;
            this.totalPaidBookings = totalPaidBookings;
        }

        public Integer getCinemaId() {
            return cinemaId;
        }

        public String getCinemaName() {
            return cinemaName;
        }

        public String getCinemaImageUrl() {
            return cinemaImageUrl;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public long getTotalTicketsSold() {
            return totalTicketsSold;
        }

        public long getTotalPaidBookings() {
            return totalPaidBookings;
        }
    }

    public static class MovieOptionRow {
        private final Integer movieId;
        private final String movieTitle;
        private final String posterUrl;

        public MovieOptionRow(Integer movieId, String movieTitle, String posterUrl) {
            this.movieId = movieId;
            this.movieTitle = movieTitle;
            this.posterUrl = posterUrl;
        }

        public Integer getMovieId() {
            return movieId;
        }

        public String getMovieTitle() {
            return movieTitle;
        }

        public String getPosterUrl() {
            return posterUrl;
        }
    }

    private static class RevenueFilter {
        private final String sql;
        private final List<Object> params;

        public RevenueFilter(String sql, List<Object> params) {
            this.sql = sql;
            this.params = params;
        }

        public String getSql() {
            return sql;
        }

        public List<Object> getParams() {
            return params;
        }
    }
}