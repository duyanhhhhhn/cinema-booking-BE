package CinemaBooking.Group2.ultis;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcrypUtil {
	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean match(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
