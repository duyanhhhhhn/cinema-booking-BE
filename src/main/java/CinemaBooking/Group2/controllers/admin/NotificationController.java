package CinemaBooking.Group2.controllers.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.notification.UserNotificationResponseDTO;
import CinemaBooking.Group2.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','STAFF')")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserNotificationResponseDTO>>> getMyNotifications(
            @RequestParam(required = false) Integer limit,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {

        List<UserNotificationResponseDTO> data = notificationService.getMyNotifications(limit, unreadOnly);
        return ResponseEntity.ok(new ApiResponse<>("Lấy danh sách thông báo thành công", data));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUnreadCount() {
        int unreadCount = notificationService.countMyUnreadNotifications();
        return ResponseEntity.ok(new ApiResponse<>("Lấy số lượng thông báo chưa đọc thành công", Map.of("unreadCount", unreadCount)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable int id) {
        notificationService.markMyNotificationAsRead(id);
        return ResponseEntity.ok(new ApiResponse<>("Đã đánh dấu thông báo là đã đọc", "OK"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllAsRead() {
        int updatedCount = notificationService.markAllMyNotificationsAsRead();
        return ResponseEntity.ok(new ApiResponse<>("Đã đánh dấu tất cả thông báo là đã đọc", Map.of("updatedCount", updatedCount)));
    }
}
