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

import CinemaBooking.Group2.models.StaffScheduleSwapRequest;
import CinemaBooking.Group2.models.Enum.StaffScheduleSwapStatus;
import jakarta.annotation.PostConstruct;

@Repository
public class StaffScheduleSwapRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    public StaffScheduleSwapRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ensureSwapRequestTable() {
        jdbcTemplate.execute("""
                create table if not exists staff_schedule_swap_request (
                    id int not null auto_increment,
                    schedule_id int not null,
                    requester_staff_id int not null,
                    target_staff_id int not null,
                    status varchar(40) not null,
                    note varchar(500) null,
                    approved_schedule_id int null,
                    created_at datetime default (now()),
                    updated_at datetime default (now()) on update current_timestamp,
                    primary key (id),
                    key idx_swap_schedule (schedule_id),
                    key idx_swap_requester_status (requester_staff_id, status),
                    key idx_swap_target_status (target_staff_id, status),
                    key idx_swap_status (status),
                    constraint fk_swap_schedule foreign key (schedule_id) references staff_schedule (id) on delete cascade,
                    constraint fk_swap_requester foreign key (requester_staff_id) references user (id),
                    constraint fk_swap_target foreign key (target_staff_id) references user (id),
                    constraint fk_swap_approved_schedule foreign key (approved_schedule_id) references staff_schedule (id)
                )
                """);
    }

    public StaffScheduleSwapRequest findById(int id) {
        List<StaffScheduleSwapRequest> rows = jdbcTemplate.query(
                "select * from staff_schedule_swap_request where id = ?",
                this::mapRow,
                id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public StaffScheduleSwapRequest findOpenByScheduleId(int scheduleId) {
        List<StaffScheduleSwapRequest> rows = jdbcTemplate.query(
                """
                select *
                from staff_schedule_swap_request
                where schedule_id = ?
                  and status in ('PENDING_STAFF_RESPONSE', 'PENDING_ADMIN_APPROVAL')
                order by id desc
                limit 1
                """,
                this::mapRow,
                scheduleId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<StaffScheduleSwapRequest> findByRequesterStaff(int requesterStaffId, StaffScheduleSwapStatus status) {
        StringBuilder sql = new StringBuilder("""
                select *
                from staff_schedule_swap_request
                where requester_staff_id = ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(requesterStaffId);

        if (status != null) {
            sql.append(" and status = ?");
            params.add(status.name());
        }

        sql.append(" order by created_at desc, id desc");
        return jdbcTemplate.query(sql.toString(), this::mapRow, params.toArray());
    }

    public List<StaffScheduleSwapRequest> findByTargetStaff(int targetStaffId, StaffScheduleSwapStatus status) {
        StringBuilder sql = new StringBuilder("""
                select *
                from staff_schedule_swap_request
                where target_staff_id = ?
                """);
        List<Object> params = new ArrayList<>();
        params.add(targetStaffId);

        if (status != null) {
            sql.append(" and status = ?");
            params.add(status.name());
        }

        sql.append(" order by created_at desc, id desc");
        return jdbcTemplate.query(sql.toString(), this::mapRow, params.toArray());
    }

    public List<StaffScheduleSwapRequest> findPendingReviewByCinema(Integer cinemaId) {
        StringBuilder sql = new StringBuilder("""
                select r.*
                from staff_schedule_swap_request r
                inner join staff_schedule s on s.id = r.schedule_id
                inner join user u on u.id = s.staff_id
                where r.status = 'PENDING_ADMIN_APPROVAL'
                """);
        List<Object> params = new ArrayList<>();

        if (cinemaId != null) {
            sql.append(" and u.cinema_id = ?");
            params.add(cinemaId);
        }

        sql.append(" order by r.created_at desc, r.id desc");
        return jdbcTemplate.query(sql.toString(), this::mapRow, params.toArray());
    }

    public int create(StaffScheduleSwapRequest item) {
        String sql = """
                insert into staff_schedule_swap_request
                    (schedule_id, requester_staff_id, target_staff_id, status, note, approved_schedule_id)
                values (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, item.getScheduleId());
            statement.setInt(2, item.getRequesterStaffId());
            statement.setInt(3, item.getTargetStaffId());
            statement.setString(4, item.getStatus().name());
            statement.setString(5, item.getNote());
            if (item.getApprovedScheduleId() == null) {
                statement.setNull(6, java.sql.Types.INTEGER);
            } else {
                statement.setInt(6, item.getApprovedScheduleId());
            }
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key == null ? 0 : key.intValue();
    }

    public int update(StaffScheduleSwapRequest item) {
        return jdbcTemplate.update(
                """
                update staff_schedule_swap_request
                set schedule_id = ?,
                    requester_staff_id = ?,
                    target_staff_id = ?,
                    status = ?,
                    note = ?,
                    approved_schedule_id = ?
                where id = ?
                """,
                item.getScheduleId(),
                item.getRequesterStaffId(),
                item.getTargetStaffId(),
                item.getStatus().name(),
                item.getNote(),
                item.getApprovedScheduleId(),
                item.getId());
    }

    private StaffScheduleSwapRequest mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        StaffScheduleSwapRequest item = new StaffScheduleSwapRequest();
        item.setId(rs.getInt("id"));
        item.setScheduleId(rs.getInt("schedule_id"));
        item.setRequesterStaffId(rs.getInt("requester_staff_id"));
        item.setTargetStaffId(rs.getInt("target_staff_id"));
        item.setStatus(StaffScheduleSwapStatus.valueOf(rs.getString("status")));
        item.setNote(rs.getString("note"));

        Integer approvedScheduleId = (Integer) rs.getObject("approved_schedule_id");
        item.setApprovedScheduleId(approvedScheduleId);

        if (rs.getTimestamp("created_at") != null) {
            item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            item.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return item;
    }
}
