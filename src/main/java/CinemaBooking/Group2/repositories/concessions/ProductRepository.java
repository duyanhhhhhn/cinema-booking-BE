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

import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.repositories.Icrud;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class ProductRepository implements Icrud<Product> {

    private final DataSource dataSource;

    @Autowired
    public ProductRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Product> getAll() {
        String sql =
            "SELECT id, name, description, price, image_url, stock, is_active, created_at " +
            "FROM " + StringValue.tbl_product + " " +
            "ORDER BY created_at DESC, id DESC";

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapProduct(rs));
            }

            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch products.", e);
        }
    }

    public List<Product> getAll(int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int offset = (page - 1) * size;
        String sql =
            "SELECT id, name, description, price, image_url, stock, is_active, created_at " +
            "FROM " + StringValue.tbl_product + " " +
            "ORDER BY created_at DESC, id DESC " +
            "LIMIT ? OFFSET ?";

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapProduct(rs));
                }
            }

            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch paged products.", e);
        }
    }

    public List<Product> getAllActive() {
        String sql =
            "SELECT id, name, description, price, image_url, stock, is_active, created_at " +
            "FROM " + StringValue.tbl_product + " " +
            "WHERE is_active = 1 " +
            "ORDER BY created_at DESC, id DESC";

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapProduct(rs));
            }

            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch active products.", e);
        }
    }

    public long countActive() {
        String sql = "SELECT COUNT(*) AS total FROM " + StringValue.tbl_product + " WHERE is_active = 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count active products.", e);
        }
    }

    @Override
    public Product findById(int id) {
        String sql =
            "SELECT id, name, description, price, image_url, stock, is_active, created_at " +
            "FROM " + StringValue.tbl_product + " " +
            "WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch product by id.", e);
        }
    }

    @Override
    public int create(Product product) {
        String sql =
            "INSERT INTO " + StringValue.tbl_product + " " +
            "(name, description, price, image_url, stock, is_active, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setString(4, product.getImageUrl());
            ps.setInt(5, product.getStock());
            ps.setInt(6, product.getIsActive());
            ps.setTimestamp(7, toTimestamp(product.getCreatedAt()));

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create product.", e);
        }
    }

    public int createReturningId(Product product) {
        String sql =
            "INSERT INTO " + StringValue.tbl_product + " " +
            "(name, description, price, image_url, stock, is_active, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setString(4, product.getImageUrl());
            ps.setInt(5, product.getStock());
            ps.setInt(6, product.getIsActive());
            ps.setTimestamp(7, toTimestamp(product.getCreatedAt()));

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
            throw new RuntimeException("Failed to create product and return id.", e);
        }
    }

    @Override
    public int update(Product product) {
        boolean hasImage = product.getImageUrl() != null && !product.getImageUrl().isBlank();

        String sql =
            "UPDATE " + StringValue.tbl_product + " " +
            "SET name = ?, description = ?, price = ?, " +
            (hasImage ? "image_url = ?, " : "") +
            "stock = ?, is_active = ? " +
            "WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            ps.setString(idx++, product.getName());
            ps.setString(idx++, product.getDescription());
            ps.setBigDecimal(idx++, product.getPrice());

            if (hasImage) {
                ps.setString(idx++, product.getImageUrl());
            }

            ps.setInt(idx++, product.getStock());
            ps.setInt(idx++, product.getIsActive());
            ps.setInt(idx, product.getId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product.", e);
        }
    }

    @Override
    public int delete(int id) {
        String sql = "DELETE FROM " + StringValue.tbl_product + " WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product.", e);
        }
    }

    public int changeActive(int id, int active) {
        String sql = "UPDATE " + StringValue.tbl_product + " SET is_active = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, active);
            ps.setInt(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to change product active state.", e);
        }
    }

    @Override
    public List<Product> search(String key) {
        String sql =
            "SELECT id, name, description, price, image_url, stock, is_active, created_at " +
            "FROM " + StringValue.tbl_product + " " +
            "WHERE name LIKE CONCAT('%', ?, '%') " +
            "ORDER BY created_at DESC, id DESC";

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key == null ? "" : key.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapProduct(rs));
                }
            }

            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search products.", e);
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setImageUrl(rs.getString("image_url"));
        product.setStock(rs.getInt("stock"));
        product.setIsActive(rs.getInt("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }

        return product;
    }

    private Timestamp toTimestamp(java.time.LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }
}
