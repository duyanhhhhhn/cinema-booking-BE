package CinemaBooking.Group2;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class CinemaBookingBeApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CinemaBookingBeApplication.class, args);
	}

}
