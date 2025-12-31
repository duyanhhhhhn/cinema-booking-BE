package CinemaBooking.Group2.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.repositories.UserRepository;

@Configuration
public class DatabaseSeeder {

    @Bean
    public CommandLineRunner initAdminAccount(
            UserRepository userRepository, 
            PasswordEncoder passwordEncoder) { 
        
        return args -> {
            String adminEmail = "admin@cinema.com"; 

            if (userRepository.existsByEmail(adminEmail)) {
                return;
            }

            User admin = new User();
            admin.setFullName("Admin System");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("123456")); 
            admin.setPhone("09090000000");
            admin.setRoleId(1); 
            admin.setIsActive(1); 
            
            
            
            userRepository.save(admin);
            System.out.println("✅ Đã tạo nhanh Admin (role_id=1) thành công!");
        };
    }
}