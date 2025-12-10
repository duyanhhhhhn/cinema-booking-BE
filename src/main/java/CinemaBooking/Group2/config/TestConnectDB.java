package CinemaBooking.Group2.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConnectDB {
    @Bean
    public CommandLineRunner testDatabaseConnection(DataSource dataSource) {
        return args -> {
            System.out.println("=== Đang kiểm tra kết nối MySQL... ===");
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("=> Kết nối MySQL THÀNH CÔNG!");
                System.out.println("=> Catalog: " + connection.getCatalog());
            } catch (SQLException e) {
                System.out.println("=> Kết nối MySQL THẤT BẠI!");
                System.out.println("=> Lý do: " + e.getMessage() + "note: anh em nhớ kiểm tra mật khẩu bên trong properties nhé");
                e.printStackTrace();
            }
            System.out.println("=== Kiểm tra kết nối MySQL xong. ===");
        };
    }
}
