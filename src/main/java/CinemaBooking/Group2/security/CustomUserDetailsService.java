package CinemaBooking.Group2.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private UserRepository userRepo;

    private String normalizeRole(String role) {
        if (role == null) return null;
        String r = role.trim().toUpperCase();
        if (r.startsWith("ROLE_")) r = r.substring(5);
        return r;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var u = userRepo.findByEmail(email);
        if (u == null) throw new UsernameNotFoundException("Không tìm thấy user");

        String normalized = normalizeRole(u.getRoleName());

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities(normalized)
                .build();
    }
}