package CinemaBooking.Group2.controllers.admin;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.security.AuthUserPrincipal;

@RestController
public class ManagerCinemaController {
	@GetMapping("/api/manager/cinema/{id}")
	public String manageCinema(@PathVariable int id) {
	    var principal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	    if (!principal.role().equals("ADMIN") && principal.cinemaId() != id) {
	        throw new RuntimeException("Bạn không có quyền quản lý rạp này!");
	    }

	    return "OK MANAGER OF CINEMA " + id;
	}
}
