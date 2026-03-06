package CinemaBooking.Group2.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.security.AuthUserPrincipal;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> out = new HashMap<>();

        out.put("authenticated", auth != null && auth.isAuthenticated());

        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof AuthUserPrincipal p) {
                Map<String, Object> pmap = new HashMap<>();
                pmap.put("email", p.email());
                pmap.put("roleClaim", p.role());
                pmap.put("cinemaId", p.cinemaId());
                out.put("principal", pmap);
            } else {
                out.put("principal", principal.toString());
            }

            List<String> auths = auth.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.toList());
            out.put("authorities", auths);
        }

        return ResponseEntity.ok(out);
    }
}
