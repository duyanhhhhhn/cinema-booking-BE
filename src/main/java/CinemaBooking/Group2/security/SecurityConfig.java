package CinemaBooking.Group2.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public routes
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(
                        		"/api/public/**",
                                "/api/auth/register/**",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/auth/refresh",
                                "/api/auth/forgot/**",
                                "/media/**",
                                "/api/public/**",
                                "/swagger-ui/index.html#/")
                        .permitAll()

                        .requestMatchers("/api/auth/password/**").authenticated()
                        .requestMatchers("/api/users/me").authenticated()

                        // ===== PUBLIC =====
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        
                        // ======= PUBLIC =======
                        .requestMatchers("/api/client/reviews/*/comment",
                        				 "/api/client/reviews/*/rating").permitAll()

                     // ===== PRIVATE (Client tạo comment) =====
                        .requestMatchers(HttpMethod.POST, "/api/client/reviews/create-comment").permitAll()
                        
                        // ===== AUTHENTICATED =====
                        .requestMatchers(
                                "/api/auth/password/**",
                                "/api/users/me")
                        .authenticated()

                        // ===== ROLE BASE =====
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/manager/**").hasAnyAuthority("ADMIN", "MANAGER")
                        .requestMatchers("/api/movies/**").hasAnyAuthority("ADMIN", "MANAGER")
                        .requestMatchers("/api/users/**").hasAnyAuthority("ADMIN", "MANAGER")
                        .requestMatchers("/api/staff/**").hasAnyAuthority("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/api/cinemas/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/api/room/**").hasAnyAuthority("ADMIN")

                        .anyRequest().authenticated())

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
