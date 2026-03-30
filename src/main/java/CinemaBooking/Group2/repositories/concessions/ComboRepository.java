package CinemaBooking.Group2.repositories.concessions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.repositories.Icrud;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class ComboRepository implements Icrud<Combo> {

    private final DataSource dataSource;

    @Autowired
    public ComboRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Combo> getAll() {
        return findAllActive();
    }

    public List<Combo> findAllIncludingInactive() {
        String sql =
            "SELECT id, name, description, price, image_url, is_active, created_at " +
            "FROM " + StringValue.tbl_combo + " " +
            "ORDER BY created_at DESC, id DESC";

        List<Combo> combos = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                combos.add(mapCombo(rs));
            }

            return combos;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch combos.", e);
        }
    }

    public List<Combo> findAllActive() {
        String sql =
            "SELECT id, name, description, price, image_url, is_active, created_at " +
            "FROM " + StringValue.tbl_combo + " " +
            "WHERE is_active = 1 " +
            "ORDER BY created_at DESC, id DESC";

        List<Combo> combos = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                combos.add(mapCombo(rs));
            }

            return combos;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch active combos.", e);
        }
    }

    public List<Combo> Paging(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int offset = (page - 1) * size;
        String sql =
            "SELECT id, name, description, price, image_url, is_active, created_at " +
            "FROM " + StringValue.tbl_combo + " " +
            "WHERE is_active = 1 " +
            "ORDER BY created_at DESC, id DESC " +
            "LIMIT ? OFFSET ?";

        List<Combo> combos = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    combos.add(mapCombo(rs));
                }
            }

            return combos;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch paged combos.", e);
        }
    }

    public long countActive() {
        String sql = "SELECT COUNT(*) AS total FROM " + StringValue.tbl_combo + " WHERE is_active = 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count active combos.", e);
        }
    }

    public long countAll() {
        String sql = "SELECT COUNT(*) AS total FROM " + StringValue.tbl_combo;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count combos.", e);
        }
    }

    @Override
    public Combo findById(int id) {
        String sql =
            "SELECT id, name, description, price, image_url, is_active, created_at " +
            "FROM " + StringValue.tbl_combo + " " +
            "WHERE id = ? AND is_active = 1";

        return findSingle(sql, id);
    }

    public Combo findByIdIncludingInactive(int id) {
        String sql =
            "SELECT id, name, description, price, image_url, is_active, created_at " +
            "FROM " + StringValue.tbl_combo + " " +
            "WHERE id = ?";

        return findSingle(sql, id);
    }

    @Override
    public int create(Combo item) {
        String sql =
            "INSERT INTO " + StringValue.tbl_combo + " " +
            "(name, description, price, image_url, is_active, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setBigDecimal(3, item.getPrice());
            ps.setString(4, item.getImageUrl());
            ps.setInt(5, item.getIsActive());
            ps.setTimestamp(6, toTimestamp(item.getCreatedAt()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return 0;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create combo.", e);
        }
    }

    @Override
    public int update(Combo item) {
        boolean hasImage = item.getImageUrl() != null && !item.getImageUrl().isBlank();

        String sql =
            "UPDATE " + StringValue.tbl_combo + " " +
            "SET name = ?, description = ?, price = ?, " +
            (hasImage ? "image_url = ?, " : "") +
            "is_active = ?, created_at = ? " +
            "WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            ps.setString(idx++, item.getName());
            ps.setString(idx++, item.getDescription());
            ps.setBigDecimal(idx++, item.getPrice());

            if (hasImage) {
                ps.setString(idx++, item.getImageUrl());
            }

            ps.setInt(idx++, item.getIsActive());
            ps.setTimestamp(idx++, toTimestamp(item.getCreatedAt()));
            ps.setInt(idx, item.getId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update combo.", e);
        }
    }

    public int updateComboPartial(Combo combo) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("UPDATE " + StringValue.tbl_combo + " SET ");

        if (combo.getName() != null && !combo.getName().trim().isEmpty()) {
            sql.append("name = ?, ");
            params.add(combo.getName().trim());
        }

        if (combo.getPrice() != null) {
            sql.append("price = ?, ");
            params.add(combo.getPrice());
        }

        if (combo.getImageUrl() != null && !combo.getImageUrl().trim().isEmpty()) {
            sql.append("image_url = ?, ");
            params.add(combo.getImageUrl().trim());
        }

        if (combo.getDescription() != null) {
            sql.append("description = ?, ");
            params.add(combo.getDescription());
        }

        if (params.isEmpty()) {
            return 0;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(combo.getId());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update combo partially.", e);
        }
    }

    @Override
    public int delete(int id) {
        String sql = "DELETE FROM " + StringValue.tbl_combo + " WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete combo.", e);
        }
    }

    public int ChangeActive(int id, int active) {
        String sql = "UPDATE " + StringValue.tbl_combo + " SET is_active = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, active);
            ps.setInt(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to change combo active state.", e);
        }
    }

    public int compareComboItem(int id, CinemaBooking.Group2.models.ComboItem item) {
        String sql =
            "SELECT id, quantity " +
            "FROM " + StringValue.tbl_comboItem + " " +
            "WHERE combo_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int itemId = rs.getInt("id");
                    int quantity = rs.getInt("quantity");

                    if (item.getId() == itemId) {
                        return item.getQuantity() == quantity ? 2 : 1;
                    }

                    if (item.getId() == 0) {
                        return 0;
                    }
                }
            }

            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to compare combo item.", e);
        }
    }

    @Override
    public List<Combo> search(String key) {
        String sql =
            "SELECT id, name, description, price, image_url, is_active, created_at " +
            "FROM " + StringValue.tbl_combo + " " +
            "WHERE name LIKE CONCAT('%', ?, '%') " +
            "ORDER BY created_at DESC, id DESC";

        List<Combo> combos = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key == null ? "" : key.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    combos.add(mapCombo(rs));
                }
            }

            return combos;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search combos.", e);
        }
    }

    private Combo findSingle(String sql, int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCombo(rs);
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch combo by id.", e);
        }
    }

    private Combo mapCombo(ResultSet rs) throws SQLException {
        Combo combo = new Combo();
        combo.setId(rs.getInt("id"));
        combo.setName(rs.getString("name"));
        combo.setDescription(rs.getString("description"));
        combo.setPrice(rs.getBigDecimal("price"));
        combo.setImageUrl(rs.getString("image_url"));
        combo.setIsActive(rs.getInt("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            combo.setCreatedAt(createdAt.toLocalDateTime());
        }

        return combo;
    }

    private Timestamp toTimestamp(java.time.LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }
}
