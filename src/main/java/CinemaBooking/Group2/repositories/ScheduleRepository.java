package CinemaBooking.Group2.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.StaffMapper;
import CinemaBooking.Group2.mappers.UserMapper;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.Enum.StaffScheduleStatus;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class ScheduleRepository implements Icrud<StaffSchedule> {

    private final JdbcTemplate jdbcTemplate;

    public ScheduleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<StaffSchedule> getAll() {
        String sql = "select * from " + StringValue.tbl_schedule + " order by work_date asc, staff_id asc, shift_id asc";
        return jdbcTemplate.query(sql, new StaffMapper());
    }

    public List<StaffSchedule> getAll(int page, int size) {
        int offset = Math.max(page - 1, 0) * Math.max(size, 1);
        String sql = "select * from " + StringValue.tbl_schedule
                + " order by work_date asc, staff_id asc, shift_id asc limit ? offset ?";
        return jdbcTemplate.query(sql, new StaffMapper(), size, offset);
    }

    public List<StaffSchedule> findByStaffAndRange(int staffId, LocalDate startDate, LocalDate endDate,
            StaffScheduleStatus status) {

        StringBuilder sql = new StringBuilder(
                "select * from " + StringValue.tbl_schedule + " where staff_id = ? and work_date between ? and ?");
        List<Object> params = new ArrayList<>();
        params.add(staffId);
        params.add(startDate);
        params.add(endDate);

        if (status != null) {
            sql.append(" and status = ?");
            params.add(status.name());
        }

        sql.append(" order by work_date asc, shift_id asc");
        return jdbcTemplate.query(sql.toString(), new StaffMapper(), params.toArray());
    }

    public StaffSchedule getSchedulesByDate(LocalDate date) {
        String sql = "select * from " + StringValue.tbl_schedule + " where work_date = ? order by id asc limit 1";
        List<StaffSchedule> rows = jdbcTemplate.query(sql, new StaffMapper(), date);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<StaffSchedule> getShedulesByRange(LocalDate startDate, LocalDate endDate) {
        String sql = "select * from " + StringValue.tbl_schedule
                + " where work_date between ? and ? order by staff_id asc, work_date asc, shift_id asc";
        return jdbcTemplate.query(sql, new StaffMapper(), startDate, endDate);
    }

    public List<StaffSchedule> getByStaffId(int staffId, LocalDate startDate, LocalDate endDate) {
        return findByStaffAndRange(staffId, startDate, endDate, null);
    }

    public List<StaffSchedule> getByShift(int shiftId) {
        String sql = "select * from " + StringValue.tbl_schedule + " where shift_id = ? order by work_date asc, id asc";
        return jdbcTemplate.query(sql, new StaffMapper(), shiftId);
    }

    public int assignSchedule(StaffSchedule item) {
        return create(item) > 0 ? 1 : 0;
    }

    public List<StaffSchedule> findByCinemaAndRange(Integer cinemaId, LocalDate startDate, LocalDate endDate,
            StaffScheduleStatus status, Integer staffId) {

        StringBuilder sql = new StringBuilder("""
                select s.*
                from staff_schedule s
                inner join user u on u.id = s.staff_id
                where u.role_id in (2, 3)
                  and s.work_date between ? and ?
                """);

        List<Object> params = new ArrayList<>();
        params.add(startDate);
        params.add(endDate);

        if (cinemaId != null) {
            sql.append(" and u.cinema_id = ?");
            params.add(cinemaId);
        }

        if (staffId != null) {
            sql.append(" and s.staff_id = ?");
            params.add(staffId);
        }

        if (status != null) {
            sql.append(" and s.status = ?");
            params.add(status.name());
        }

        sql.append(" order by s.work_date asc, s.shift_id asc, s.staff_id asc");
        return jdbcTemplate.query(sql.toString(), new StaffMapper(), params.toArray());
    }

    public List<StaffSchedule> findByStaffAndDate(int staffId, LocalDate workDate) {
        String sql = "select * from " + StringValue.tbl_schedule
                + " where staff_id = ? and work_date = ? order by shift_id asc, id asc";
        return jdbcTemplate.query(sql, new StaffMapper(), staffId, workDate);
    }

    public StaffSchedule findByStaffShiftAndDate(int staffId, int shiftId, LocalDate workDate) {
        String sql = "select * from " + StringValue.tbl_schedule
                + " where staff_id = ? and shift_id = ? and work_date = ? order by id desc limit 1";
        List<StaffSchedule> rows = jdbcTemplate.query(sql, new StaffMapper(), staffId, shiftId, workDate);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public boolean existsByShiftId(int shiftId) {
        String sql = "select count(*) from " + StringValue.tbl_schedule + " where shift_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, shiftId);
        return count != null && count > 0;
    }

    public int updateStatus(int id, StaffScheduleStatus status) {
        String sql = "update " + StringValue.tbl_schedule + " set status = ? where id = ?";
        return jdbcTemplate.update(sql, status.name(), id);
    }

    public List<StaffSchedule> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = String.join(",", ids.stream().map(id -> "?").toList());
        String sql = "select * from " + StringValue.tbl_schedule + " where id in (" + placeholders + ")";
        return jdbcTemplate.query(sql, new StaffMapper(), ids.toArray());
    }

    @Override
    public StaffSchedule findById(int id) {
        String sql = "select * from " + StringValue.tbl_schedule + " where id = ?";
        List<StaffSchedule> rows = jdbcTemplate.query(sql, new StaffMapper(), id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    @Override
    public List<StaffSchedule> search(String key) {
        return Collections.emptyList();
    }

    @Override
    public int create(StaffSchedule item) {
        String sql = "insert into " + StringValue.tbl_schedule + " (staff_id, shift_id, work_date, status) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, item.getStaffId());
            statement.setInt(2, item.getShiftId());
            statement.setObject(3, item.getWorkDate());
            statement.setString(4, item.getStatus().name());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key == null ? 0 : key.intValue();
    }

    @Override
    public int update(StaffSchedule item) {
        if (item == null || item.getId() <= 0) {
            return 0;
        }

        String sql = "update " + StringValue.tbl_schedule
                + " set staff_id = ?, shift_id = ?, work_date = ?, status = ? where id = ?";

        return jdbcTemplate.update(sql,
                item.getStaffId(),
                item.getShiftId(),
                item.getWorkDate(),
                item.getStatus().name(),
                item.getId());
    }

    @Override
    public int delete(int id) {
        String sql = "delete from " + StringValue.tbl_schedule + " where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public List<User> getAllStaff() {
        String sql = """
                select u.*, r.name as role_name
                from user u
                inner join role r on u.role_id = r.id
                where u.role_id = 3
                order by u.full_name asc
                """;
        return jdbcTemplate.query(sql, new UserMapper());
    }

    public User findStaffById(int id) {
        String sql = """
                select u.*, r.name as role_name
                from user u
                inner join role r on u.role_id = r.id
                where u.id = ?
                """;
        List<User> rows = jdbcTemplate.query(sql, new UserMapper(), id);
        return rows.isEmpty() ? null : rows.get(0);
    }
}
