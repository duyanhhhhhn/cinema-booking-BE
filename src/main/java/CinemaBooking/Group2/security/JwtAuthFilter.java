//package CinemaBooking.Group2.security;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.security.core.GrantedAuthority;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtService jwtService;
//
//    // Các endpoint public
//    private static final List<String> PUBLIC_PATHS = List.of(
//            "/api/auth",
//            "/api/auth/login",
//            "/api/auth/register",
//            "/api/auth/forgot",
//
//            "/api/public",
//            "/api/client/reviews",
//
//            "/api/showtimes/public",
//            "/api/showtimes-seat/",
//         "/api/payment/**",
//
//            "/swagger-ui",
//            "/swagger-ui.html",
//            "/v3/api-docs",
//
//            "/media",
//            "/uploads",
//            "/static",
//            "/favicon.ico");
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getServletPath();
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            return true;
//        }
//        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        // Không có Authorization header
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = authHeader.substring(7);
//
//        // Token không hợp lệ
//        if (!jwtService.validateToken(token)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // Token hợp lệ → set SecurityContext
//        if (SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            String email = jwtService.getEmail(token);
//            String role = jwtService.getRole(token);
//            Integer cinemaId = jwtService.getCinemaId(token);
//
//            AuthUserPrincipal principal = new AuthUserPrincipal(email, role, cinemaId);
//            List<GrantedAuthority> authorities = new ArrayList<>();
//            if (role != null && !role.isBlank()) {
//                // Normalize role: trim, uppercase and remove optional "ROLE_" prefix
//                String normalized = role.trim().toUpperCase();
//                if (normalized.startsWith("ROLE_")) {
//                    normalized = normalized.substring(5);
//                }
//                // Add both forms so checks using hasAuthority('MANAGER') or hasRole('MANAGER') work
//                authorities.add(new SimpleGrantedAuthority(normalized));
//                authorities.add(new SimpleGrantedAuthority("ROLE_" + normalized));
//            }
//
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    principal,
//                    null,
//                    authorities);
//
//            authentication.setDetails(
//                    new WebAuthenticationDetailsSource().buildDetails(request));
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
package CinemaBooking.Group2.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    // ===== FIX =====
    // Không dùng "/api/auth"
    // vì sẽ làm skip luôn API đổi mật khẩu
    private static final List<String> PUBLIC_PATHS = List.of(

            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/forgot",
            "/api/auth/refresh",

            "/api/public",

            "/api/client/reviews",

            "/api/showtimes/public",
            "/api/showtimes-seat",

            "/api/payment",

            "/swagger-ui",
            "/swagger-ui.html",
            "/v3/api-docs",

            "/media",
            "/uploads",
            "/static",

            "/favicon.ico"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        // ===== FIX =====
        // Cho phép OPTIONS request đi qua
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // ===== FIX =====
        // Không dùng path::startsWith trực tiếp
        // vì "/api/auth" sẽ match tất cả
        return PUBLIC_PATHS.stream().anyMatch(publicPath ->
                path.equals(publicPath)
                        || path.startsWith(publicPath + "/")
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        String authHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        // ===== DEBUG =====
        System.out.println("=================================");
        System.out.println("JWT FILTER RUNNING");
        System.out.println("PATH: " + path);
        System.out.println("AUTH HEADER: " + authHeader);
        System.out.println("=================================");

        // =====================================================
        // KHÔNG CÓ TOKEN
        // =====================================================

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }

        String token = authHeader.substring(7);

        // =====================================================
        // TOKEN INVALID
        // =====================================================

        if (!jwtService.validateToken(token)) {

            System.out.println("TOKEN INVALID");

            filterChain.doFilter(request, response);

            return;
        }

        // =====================================================
        // TOKEN VALID
        // =====================================================

        if (SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            String email = jwtService.getEmail(token);

            String role = jwtService.getRole(token);

            Integer cinemaId =
                    jwtService.getCinemaId(token);

            AuthUserPrincipal principal =
                    new AuthUserPrincipal(
                            email,
                            role,
                            cinemaId
                    );

            List<GrantedAuthority> authorities =
                    new ArrayList<>();

            if (role != null && !role.isBlank()) {

                // ===== FIX =====
                // normalize role
                String normalized =
                        role.trim().toUpperCase();

                if (normalized.startsWith("ROLE_")) {
                    normalized = normalized.substring(5);
                }

                // ===== FIX =====
                // hỗ trợ cả hasAuthority và hasRole
                authorities.add(
                        new SimpleGrantedAuthority(normalized)
                );

                authorities.add(
                        new SimpleGrantedAuthority(
                                "ROLE_" + normalized
                        )
                );
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            // ===== DEBUG =====
            System.out.println("AUTHENTICATED USER: " + email);
        }

        filterChain.doFilter(request, response);
    }
}