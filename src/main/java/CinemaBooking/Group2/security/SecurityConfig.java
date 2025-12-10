package CinemaBooking.Group2.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            	    // Public routes
            	    .requestMatchers(
            	        "/api/auth/register/**",
            	        "/api/auth/login",
            	        "/api/auth/refresh",
            	        "/api/auth/logout",
            	        "/api/auth/forgot/**"
            	    ).permitAll()

            	    // Protected (login required)
            	    .requestMatchers("/api/auth/password/**").authenticated()

            	    // Roles
            	    .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
            	    .requestMatchers("/api/manager/**").hasAnyAuthority("ADMIN","MANAGER")
            	    .requestMatchers("/api/staff/**").hasAnyAuthority("ADMIN","MANAGER","STAFF")

            	    .anyRequest().authenticated()
            	)

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
}
