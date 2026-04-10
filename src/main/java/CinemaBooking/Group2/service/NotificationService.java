package CinemaBooking.Group2.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import CinemaBooking.Group2.dtos.notification.UserNotificationResponseDTO;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.UserNotification;
import CinemaBooking.Group2.repositories.UserNotificationRepository;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.security.AuthUserPrincipal;

@Service
public class NotificationService {

    private static final ZoneId ZONE_VN = ZoneId.of("Asia/Ho_Chi_Minh");

    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public NotificationService(
            UserNotificationRepository userNotificationRepository,
            UserRepository userRepository,
            AuthService authService) {
        this.userNotificationRepository = userNotificationRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public List<UserNotificationResponseDTO> getMyNotifications(Integer limit, boolean unreadOnly) {
        User currentUser = requireCurrentUser();
        List<UserNotification> rows = userNotificationRepository.findByRecipient(
                currentUser.getId(),
                unreadOnly,
                limit);

        List<UserNotificationResponseDTO> data = new ArrayList<>();
        for (UserNotification row : rows) {
            data.add(toResponse(row));
        }
        return data;
    }

    public int countMyUnreadNotifications() {
        User currentUser = requireCurrentUser();
        Integer count = userNotificationRepository.countUnreadByRecipient(currentUser.getId());
        return count == null ? 0 : count;
    }

    @Transactional
    public void markMyNotificationAsRead(int notificationId) {
        User currentUser = requireCurrentUser();
        int updated = userNotificationRepository.markAsRead(
                notificationId,
                currentUser.getId(),
                LocalDateTime.now(ZONE_VN));
        if (updated <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo");
        }
    }

    @Transactional
    public int markAllMyNotificationsAsRead() {
        User currentUser = requireCurrentUser();
        return userNotificationRepository.markAllAsRead(
                currentUser.getId(),
                LocalDateTime.now(ZONE_VN));
    }

    @Transactional
    public void createNotification(
            int recipientUserId,
            String type,
            String title,
            String message,
            String actionUrl,
            String actionLabel,
            String relatedEntityType,
            Integer relatedEntityId) {

        UserNotification notification = new UserNotification();
        notification.setRecipientUserId(recipientUserId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setActionUrl(actionUrl);
        notification.setActionLabel(actionLabel);
        notification.setRelatedEntityType(relatedEntityType);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now(ZONE_VN));

        int notificationId = userNotificationRepository.create(notification);
        if (notificationId <= 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tạo thông báo");
        }
    }

    private User requireCurrentUser() {
        AuthUserPrincipal principal = authService.getPrincipal();
        User user = userRepository.findByEmail(principal.email());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy thông tin đăng nhập");
        }
        return user;
    }

    private UserNotificationResponseDTO toResponse(UserNotification item) {
        UserNotificationResponseDTO dto = new UserNotificationResponseDTO();
        dto.setId(item.getId());
        dto.setType(item.getType());
        dto.setTitle(item.getTitle());
        dto.setMessage(item.getMessage());
        dto.setActionUrl(item.getActionUrl());
        dto.setActionLabel(item.getActionLabel());
        dto.setRelatedEntityType(item.getRelatedEntityType());
        dto.setRelatedEntityId(item.getRelatedEntityId());
        dto.setRead(item.isRead());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setReadAt(item.getReadAt());
        return dto;
    }
}
