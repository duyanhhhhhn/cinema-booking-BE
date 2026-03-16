package CinemaBooking.Group2.repositories;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.price_adjustment.admin.AdminPriceAdjustmentDto;
import CinemaBooking.Group2.dtos.price_adjustment.admin.AdminPriceAdjustmentFilterDto;
import CinemaBooking.Group2.dtos.price_adjustment.admin.AdminPriceAdjustmentUpsertDto;

@Repository
public class PriceAdjustmentAdminRepository {

    private final DataSource dataSource;

    @Autowired
    public PriceAdjustmentAdminRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<AdminPriceAdjustmentDto> findAll(AdminPriceAdjustmentFilterDto filter) {
        List<AdminPriceAdjustmentDto> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, name, adjustment_type, value, apply_on_days, start_date, end_date, is_active ");
        sql.append("FROM price_adjustment ");
        sql.append("WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();

        if (filter != null && filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
            sql.append("AND LOWER(name) LIKE ? ");
            params.add("%" + filter.getKeyword().trim().toLowerCase() + "%");
        }

        if (filter != null && filter.getIsActive() != null) {
            sql.append("AND is_active = ? ");
            params.add(filter.getIsActive());
        }

        sql.append("ORDER BY id DESC");

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {
            setParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi findAll price_adjustment", e);
        }

        return list;
    }

    public AdminPriceAdjustmentDto findById(Integer id) {
        String sql = "SELECT id, name, adjustment_type, value, apply_on_days, start_date, end_date, is_active "
                   + "FROM price_adjustment WHERE id = ?";

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi findById price_adjustment", e);
        }

        return null;
    }

    public Integer create(AdminPriceAdjustmentUpsertDto dto) {
        String sql = "INSERT INTO price_adjustment "
                   + "(name, adjustment_type, value, apply_on_days, start_date, end_date, is_active) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getAdjustmentType());
            ps.setBigDecimal(3, dto.getValue());
            ps.setString(4, emptyToNull(dto.getApplyOnDays()));

            if (dto.getStartDate() != null) {
                ps.setDate(5, Date.valueOf(dto.getStartDate()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            if (dto.getEndDate() != null) {
                ps.setDate(6, Date.valueOf(dto.getEndDate()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            ps.setBoolean(7, Boolean.TRUE.equals(dto.getIsActive()));

            int affected = ps.executeUpdate();
            if (affected <= 0) {
                return null;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi create price_adjustment", e);
        }

        return null;
    }

    public boolean update(Integer id, AdminPriceAdjustmentUpsertDto dto) {
        String sql = "UPDATE price_adjustment "
                   + "SET name = ?, adjustment_type = ?, value = ?, apply_on_days = ?, start_date = ?, end_date = ?, is_active = ? "
                   + "WHERE id = ?";

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getAdjustmentType());
            ps.setBigDecimal(3, dto.getValue());
            ps.setString(4, emptyToNull(dto.getApplyOnDays()));

            if (dto.getStartDate() != null) {
                ps.setDate(5, Date.valueOf(dto.getStartDate()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            if (dto.getEndDate() != null) {
                ps.setDate(6, Date.valueOf(dto.getEndDate()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            ps.setBoolean(7, Boolean.TRUE.equals(dto.getIsActive()));
            ps.setInt(8, id);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi update price_adjustment", e);
        }
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM price_adjustment WHERE id = ?";

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi delete price_adjustment", e);
        }
    }

    public boolean updateActive(Integer id, boolean isActive) {
        String sql = "UPDATE price_adjustment SET is_active = ? WHERE id = ?";

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi updateActive price_adjustment", e);
        }
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM price_adjustment WHERE id = ?";

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi existsById price_adjustment", e);
        }

        return false;
    }

    public boolean existsActiveOverlapDays(Integer excludeId, String adjustmentType, String applyOnDays) {
        if (applyOnDays == null || applyOnDays.trim().isEmpty()) {
            return false;
        }

        String[] days = applyOnDays.split(",");
        List<String> cleanDays = new ArrayList<>();
        for (String day : days) {
            if (day != null && !day.trim().isEmpty()) {
                cleanDays.add(day.trim());
            }
        }

        if (cleanDays.isEmpty()) {
            return false;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM price_adjustment ");
        sql.append("WHERE is_active = 1 ");
        sql.append("AND adjustment_type = ? ");
        sql.append("AND apply_on_days IS NOT NULL ");

        if (excludeId != null) {
            sql.append("AND id <> ? ");
        }

        sql.append("AND (");
        for (int i = 0; i < cleanDays.size(); i++) {
            if (i > 0) {
                sql.append(" OR ");
            }
            sql.append("FIND_IN_SET(?, apply_on_days) > 0");
        }
        sql.append(")");

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {
            int index = 1;
            ps.setString(index++, adjustmentType);

            if (excludeId != null) {
                ps.setInt(index++, excludeId);
            }

            for (String day : cleanDays) {
                ps.setString(index++, day);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi existsActiveOverlapDays price_adjustment", e);
        }

        return false;
    }

    public boolean existsActiveOverlapDateRange(Integer excludeId, String adjustmentType, LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM price_adjustment ");
        sql.append("WHERE is_active = 1 ");
        sql.append("AND adjustment_type = ? ");
        sql.append("AND start_date IS NOT NULL ");
        sql.append("AND end_date IS NOT NULL ");
        sql.append("AND NOT (end_date < ? OR start_date > ?) ");

        if (excludeId != null) {
            sql.append("AND id <> ? ");
        }

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {
            int index = 1;
            ps.setString(index++, adjustmentType);
            ps.setDate(index++, Date.valueOf(startDate));
            ps.setDate(index++, Date.valueOf(endDate));

            if (excludeId != null) {
                ps.setInt(index++, excludeId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi existsActiveOverlapDateRange price_adjustment", e);
        }

        return false;
    }

    private AdminPriceAdjustmentDto mapRow(ResultSet rs) throws Exception {
        AdminPriceAdjustmentDto dto = new AdminPriceAdjustmentDto();
        dto.setId(rs.getInt("id"));
        dto.setName(rs.getString("name"));
        dto.setAdjustmentType(rs.getString("adjustment_type"));
        dto.setValue(rs.getBigDecimal("value"));
        dto.setApplyOnDays(rs.getString("apply_on_days"));

        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            dto.setStartDate(startDate.toLocalDate());
        }

        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            dto.setEndDate(endDate.toLocalDate());
        }

        dto.setIsActive(rs.getBoolean("is_active"));
        return dto;
    }

    private void setParams(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
