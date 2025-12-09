package CinemaBooking.Group2.controllers.test;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRoleController {

    // Ai cũng truy cập được
    @GetMapping("/api/auth/public")
    public String publicApi() {
        return "📢 Public API: Ai cũng truy cập được!";
    }

    // Chỉ ADMIN truy cập
    @GetMapping("/api/admin/info")
    public String adminApi() {
        return "🛡️ Admin API: Chỉ ADMIN được phép!";
    }

    // ADMIN + MANAGER truy cập
    @GetMapping("/api/manager/info")
    public String managerApi() {
        return "📌 Manager API: ADMIN & MANAGER được phép!";
    }
}

