package CinemaBooking.Group2.security;

public record AuthUserPrincipal(String email, String role, Integer cinemaId) {}
