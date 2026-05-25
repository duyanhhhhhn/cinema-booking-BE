package CinemaBooking.Group2.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.StaffScheduleUrgentRequest;
import CinemaBooking.Group2.models.Enum.StaffScheduleUrgentRequestStatus;
import CinemaBooking.Group2.models.Enum.StaffScheduleUrgentRequestType;
import jakarta.annotation.PostConstruct;

@Repository
public class StaffScheduleUrgentRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    public StaffScheduleUrgentRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ensureUrgentRequestTable() {
        jdbcTemplate.execute("""
                create table if not exists staff_schedule_urgent_request (
                    id int not null auto_increment,
                    schedule_id int not null,
                    requester_staff_id int not null,
                    type varchar(40) not null,
                    status varchar(40) not null,
                    reason varchar(500) not null,
                    expected_arrival_time time null,
                    created_at datetime default (now()),
                    updated_at datetime default (now()) on update current_timestamp,
                    primary key (id),
                    key idx_urgent_schedule (schedule_id),
                    key idx_urgent_requester_status (requester_staff_id, status),
                    key idx_urgent_status (status),
                    constraint fk_urgent_schedule foreign key (schedule_id) references staff_schedule (id) on delete cascade,
                    constraint fk_urgent_requester foreign key (requester_staff_id) references user (id)
                )
                """);
    }

    public StaffScheduleUrgentRequest findById(int id) {
        List<StaffScheduleUrgentRequest> rows = jdbcTemplate.query(
                "select * from staff_schedule_urgent_request where id = ?",
                this::mapRow,
                id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public StaffScheduleUrgentRequest findOpenByScheduleId(int scheduleId) {
        List<StaffScheduleUrgentRequest> rows = jdbcTemplate.query(
                """
                select *
                from staff_schedule_urgent_request
                where schedule_id = ?
                  and status = 'PENDING_ADMIN_APPROVAL'
                order by id desc
                limit 1
                """,
                this::mapRow,
                scheduleId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public StaffScheduleUrgentRequest findLatestApprovedByScheduleId(int scheduleId) {
        List<StaffScheduleUrgentRequest> rows = jdbcTemplate.query(
                """
                select *
                from staff_schedule_urgent_request
                where schedule_id = ?
                  and status = 'ADMIN_APPROVED'
                order by id desc
                limit 1
                """,
                this::mapRow,
                scheduleId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<StaffScheduleUrgentRequest> findByRequesterStaff(
            int requesterStaffId,
            StaffScheduleUrgentRequestStatus status) {

        StringBuilder sql = new StringBuilder("""
                select *
                from staff_schedule_urgent_request
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

    public List<StaffScheduleUrgentRequest> findByReviewCinema(
            Integer cinemaId,
            StaffScheduleUrgentRequestStatus status) {

        StringBuilder sql = new StringBuilder("""
                select r.*
                from staff_schedule_urgent_request r
                inner join staff_schedule s on s.id = r.schedule_id
                inner join user u on u.id = s.staff_id
                where 1 = 1
                """);
        List<Object> params = new ArrayList<>();

        if (cinemaId != null) {
            sql.append(" and u.cinema_id = ?");
            params.add(cinemaId);
        }

        if (status != null) {
            sql.append(" and r.status = ?");
            params.add(status.name());
        }

        sql.append(" order by r.created_at desc, r.id desc");
        return jdbcTemplate.query(sql.toString(), this::mapRow, params.toArray());
    }

    public int create(StaffScheduleUrgentRequest item) {
        String sql = """
                insert into staff_schedule_urgent_request
                    (schedule_id, requester_staff_id, type, status, reason, expected_arrival_time)
                values (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, item.getScheduleId());
            statement.setInt(2, item.getRequesterStaffId());
            statement.setString(3, item.getType().name());
            statement.setString(4, item.getStatus().name());
            statement.setString(5, item.getReason());
            if (item.getExpectedArrivalTime() == null) {
                statement.setNull(6, java.sql.Types.TIME);
            } else {
                statement.setTime(6, java.sql.Time.valueOf(item.getExpectedArrivalTime()));
            }
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key == null ? 0 : key.intValue();
    }

    public int update(StaffScheduleUrgentRequest item) {
        return jdbcTemplate.update(
                """
                update staff_schedule_urgent_request
                set schedule_id = ?,
                    requester_staff_id = ?,
                    type = ?,
                    status = ?,
                    reason = ?,
                    expected_arrival_time = ?
                where id = ?
                """,
                item.getScheduleId(),
                item.getRequesterStaffId(),
                item.getType().name(),
                item.getStatus().name(),
                item.getReason(),
                item.getExpectedArrivalTime() == null ? null : java.sql.Time.valueOf(item.getExpectedArrivalTime()),
                item.getId());
    }

    private StaffScheduleUrgentRequest mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        StaffScheduleUrgentRequest item = new StaffScheduleUrgentRequest();
        item.setId(rs.getInt("id"));
        item.setScheduleId(rs.getInt("schedule_id"));
        item.setRequesterStaffId(rs.getInt("requester_staff_id"));
        item.setType(StaffScheduleUrgentRequestType.valueOf(rs.getString("type")));
        item.setStatus(StaffScheduleUrgentRequestStatus.valueOf(rs.getString("status")));
        item.setReason(rs.getString("reason"));

        java.sql.Time expectedArrivalTime = rs.getTime("expected_arrival_time");
        if (expectedArrivalTime != null) {
            item.setExpectedArrivalTime(expectedArrivalTime.toLocalTime());
        }
        if (rs.getTimestamp("created_at") != null) {
            item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            item.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return item;
    }
}
