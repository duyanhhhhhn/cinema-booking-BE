package CinemaBooking.Group2.repositories.concessions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.ComboItem;
import CinemaBooking.Group2.repositories.Icrud;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class ComboItemRepository implements Icrud<ComboItem> {

    private final DataSource dataSource;

    @Autowired
    public ComboItemRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ComboItem> getAll() {
        String sql =
            "SELECT id, combo_id, product_id, quantity " +
            "FROM " + StringValue.tbl_comboItem + " " +
            "ORDER BY combo_id ASC, id ASC";

        List<ComboItem> items = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(mapComboItem(rs));
            }

            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch combo items.", e);
        }
    }

    @Override
    public ComboItem findById(int id) {
        String sql =
            "SELECT id, combo_id, product_id, quantity " +
            "FROM " + StringValue.tbl_comboItem + " " +
            "WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapComboItem(rs);
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch combo item by id.", e);
        }
    }

    public List<ComboItem> getByCombo(int comboId) {
        String sql =
            "SELECT id, combo_id, product_id, quantity " +
            "FROM " + StringValue.tbl_comboItem + " " +
            "WHERE combo_id = ? " +
            "ORDER BY id ASC";

        List<ComboItem> items = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, comboId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapComboItem(rs));
                }
            }

            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch combo items by combo id.", e);
        }
    }

    public List<String> findComboNamesByProductId(int productId) {
        String sql =
            "SELECT DISTINCT c.name " +
            "FROM " + StringValue.tbl_comboItem + " ci " +
            "JOIN " + StringValue.tbl_combo + " c ON c.id = ci.combo_id " +
            "WHERE ci.product_id = ? " +
            "ORDER BY c.created_at DESC, c.id DESC";

        List<String> comboNames = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comboNames.add(rs.getString("name"));
                }
            }

            return comboNames;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch combo names by product id.", e);
        }
    }

    public int deleteByCombo(int idCombo) {
        String sql = "DELETE FROM " + StringValue.tbl_comboItem + " WHERE combo_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCombo);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete combo items by combo id.", e);
        }
    }

    @Override
    public int create(ComboItem item) {
        ComboItem existing = findByComboAndProduct(item.getComboId(), item.getProductId());
        if (existing != null) {
            item.setId(existing.getId());
            return update(item);
        }

        String sql =
            "INSERT INTO " + StringValue.tbl_comboItem + " " +
            "(combo_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getComboId());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create combo item.", e);
        }
    }

    @Override
    public int update(ComboItem item) {
        String sql =
            "UPDATE " + StringValue.tbl_comboItem + " " +
            "SET product_id = ?, quantity = ? " +
            "WHERE id = ? AND combo_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getProductId());
            ps.setInt(2, item.getQuantity());
            ps.setInt(3, item.getId());
            ps.setInt(4, item.getComboId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update combo item.", e);
        }
    }

    public int AddQuantity(ComboItem item, int quantity) {
        String sql = "UPDATE " + StringValue.tbl_comboItem + " SET quantity = ? WHERE id = ?";
        int total = quantity + item.getQuantity();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, total);
            ps.setInt(2, item.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add combo item quantity.", e);
        }
    }

    @Override
    public int delete(int id) {
        String sql = "DELETE FROM " + StringValue.tbl_comboItem + " WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete combo item.", e);
        }
    }

    @Override
    public List<ComboItem> search(String key) {
        return List.of();
    }

    public boolean check(ComboItem item) {
        return findByComboAndProduct(item.getComboId(), item.getProductId()) != null;
    }

    public boolean checkExist(int id) {
        return findById(id) != null;
    }

    private ComboItem findByComboAndProduct(int comboId, int productId) {
        String sql =
            "SELECT id, combo_id, product_id, quantity " +
            "FROM " + StringValue.tbl_comboItem + " " +
            "WHERE combo_id = ? AND product_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, comboId);
            ps.setInt(2, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapComboItem(rs);
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch combo item by combo and product.", e);
        }
    }

    private ComboItem mapComboItem(ResultSet rs) throws SQLException {
        ComboItem item = new ComboItem();
        item.setId(rs.getInt("id"));
        item.setComboId(rs.getInt("combo_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    }
}
