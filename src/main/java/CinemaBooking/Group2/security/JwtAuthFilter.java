package CinemaBooking.Group2.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {

        try {
            String header = request.getHeader("Authorization");

            // Check Bearer token
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                // Validate token
                if (jwtService.validateToken(token) && 
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                    String email = jwtService.getEmail(token);
                    String role = jwtService.getRole(token);
                    Integer cinemaId = jwtService.getCinemaId(token);

                    var auth = new UsernamePasswordAuthenticationToken(
                            new AuthUserPrincipal(email, role, cinemaId),
                            null,
                            java.util.List.of(() -> role)
                    );

                    auth.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            // Token invalid → clear context, avoid 500 error
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}


