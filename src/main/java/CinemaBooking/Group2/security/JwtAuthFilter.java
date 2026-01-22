package CinemaBooking.Group2.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    // Các endpoint public
    private static final List<String> PUBLIC_PATHS = List.of(
    		  "/api/auth",
              "/api/public",
              "/api/client/reviews",
              "/api/showtimes/public",
              "/api/showtimes-seat/",
              "/swagger-ui",
              "/v3/api-docs",
              "/swagger-ui.html",
              "/media",
              "/uploads",
              "/static",
              "/favicon.ico",
            "/swagger-ui",
            "/v3/api-docs"
            
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Không có Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String token = authHeader.substring(7);

        // Token không hợp lệ
        if (!jwtService.validateToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // Token hợp lệ → set SecurityContext
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            String email = jwtService.getEmail(token);
            String role = jwtService.getRole(token);
            Integer cinemaId = jwtService.getCinemaId(token);

            AuthUserPrincipal principal =
                    new AuthUserPrincipal(email, role, cinemaId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(() -> role)
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
