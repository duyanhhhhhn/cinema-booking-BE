package CinemaBooking.Group2.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.UserNotification;
import jakarta.annotation.PostConstruct;

@Repository
public class UserNotificationRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserNotificationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ensureNotificationTable() {
        jdbcTemplate.execute("""
                create table if not exists user_notification (
                    id int not null auto_increment,
                    recipient_user_id int not null,
                    type varchar(80) not null,
                    title varchar(255) not null,
                    message varchar(1000) not null,
                    action_url varchar(500) null,
                    action_label varchar(120) null,
                    related_entity_type varchar(80) null,
                    related_entity_id int null,
                    is_read tinyint(1) not null default 0,
                    created_at datetime not null default (now()),
                    read_at datetime null,
                    primary key (id),
                    key idx_notification_recipient_read (recipient_user_id, is_read),
                    key idx_notification_created (created_at),
                    constraint fk_notification_recipient_user
                        foreign key (recipient_user_id) references user (id) on delete cascade
                )
                """);
    }

    public List<UserNotification> findByRecipient(int recipientUserId, boolean unreadOnly, Integer limit) {
        StringBuilder sql = new StringBuilder("""
                select *
                from user_notification
                where recipient_user_id = ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(recipientUserId);

        if (unreadOnly) {
            sql.append(" and is_read = 0");
        }

        sql.append(" order by created_at desc, id desc");
        if (limit != null && limit > 0) {
            sql.append(" limit ?");
            params.add(limit);
        }

        return jdbcTemplate.query(sql.toString(), this::mapRow, params.toArray());
    }

    public Integer countUnreadByRecipient(int recipientUserId) {
        return jdbcTemplate.queryForObject(
                """
                select count(*)
                from user_notification
                where recipient_user_id = ?
                  and is_read = 0
                """,
                Integer.class,
                recipientUserId);
    }

    public int create(UserNotification item) {
        String sql = """
                insert into user_notification
                    (recipient_user_id, type, title, message, action_url, action_label, related_entity_type, related_entity_id, is_read, created_at, read_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, item.getRecipientUserId());
            statement.setString(2, item.getType());
            statement.setString(3, item.getTitle());
            statement.setString(4, item.getMessage());
            statement.setString(5, item.getActionUrl());
            statement.setString(6, item.getActionLabel());
            statement.setString(7, item.getRelatedEntityType());
            if (item.getRelatedEntityId() == null) {
                statement.setNull(8, java.sql.Types.INTEGER);
            } else {
                statement.setInt(8, item.getRelatedEntityId());
            }
            statement.setBoolean(9, item.isRead());
            statement.setObject(10, item.getCreatedAt());
            statement.setObject(11, item.getReadAt());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key == null ? 0 : key.intValue();
    }

    public int markAsRead(int notificationId, int recipientUserId, LocalDateTime readAt) {
        return jdbcTemplate.update(
                """
                update user_notification
                set is_read = 1,
                    read_at = coalesce(read_at, ?)
                where id = ?
                  and recipient_user_id = ?
                """,
                readAt,
                notificationId,
                recipientUserId);
    }

    public int markAllAsRead(int recipientUserId, LocalDateTime readAt) {
        return jdbcTemplate.update(
                """
                update user_notification
                set is_read = 1,
                    read_at = coalesce(read_at, ?)
                where recipient_user_id = ?
                  and is_read = 0
                """,
                readAt,
                recipientUserId);
    }

    private UserNotification mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        UserNotification item = new UserNotification();
        item.setId(rs.getInt("id"));
        item.setRecipientUserId(rs.getInt("recipient_user_id"));
        item.setType(rs.getString("type"));
        item.setTitle(rs.getString("title"));
        item.setMessage(rs.getString("message"));
        item.setActionUrl(rs.getString("action_url"));
        item.setActionLabel(rs.getString("action_label"));
        item.setRelatedEntityType(rs.getString("related_entity_type"));
        item.setRelatedEntityId((Integer) rs.getObject("related_entity_id"));
        item.setRead(rs.getBoolean("is_read"));

        if (rs.getTimestamp("created_at") != null) {
            item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("read_at") != null) {
            item.setReadAt(rs.getTimestamp("read_at").toLocalDateTime());
        }
        return item;
    }
}
