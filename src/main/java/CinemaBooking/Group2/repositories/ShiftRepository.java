package CinemaBooking.Group2.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.WorkShift;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class ShiftRepository implements Icrud<WorkShift> {

    private final JdbcTemplate jdbcTemplate;

    public ShiftRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class ShiftMapper implements RowMapper<WorkShift> {

        @Override
        public WorkShift mapRow(ResultSet rs, int rowNum) throws SQLException {
            WorkShift item = new WorkShift();
            item.setId(rs.getInt("id"));
            item.setName(rs.getString("name"));
            item.setStartTime(rs.getTime("start_time").toLocalTime());
            item.setEndTime(rs.getTime("end_time").toLocalTime());
            if (rs.getTimestamp("created_at") != null) {
                item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            return item;
        }
    }

    @Override
    public List<WorkShift> getAll() {
        String sql = "select * from " + StringValue.tbl_shift + " order by start_time asc, id asc";
        return jdbcTemplate.query(sql, new ShiftMapper());
    }

    @Override
    public WorkShift findById(int id) {
        String sql = "select * from " + StringValue.tbl_shift + " where id = ?";
        List<WorkShift> rows = jdbcTemplate.query(sql, new ShiftMapper(), id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<WorkShift> findByStaffId(int staffId) {
        String sql = """
                select distinct ws.*
                from work_shift ws
                inner join staff_schedule ss on ss.shift_id = ws.id
                where ss.staff_id = ?
                order by ws.start_time asc, ws.id asc
                """;
        return jdbcTemplate.query(sql, new ShiftMapper(), staffId);
    }

    public WorkShift getByDate(LocalDate date) {
        String sql = """
                select ws.*
                from work_shift ws
                inner join staff_schedule ss on ss.shift_id = ws.id
                where ss.work_date = ?
                order by ws.start_time asc, ws.id asc
                limit 1
                """;
        List<WorkShift> rows = jdbcTemplate.query(sql, new ShiftMapper(), date);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public WorkShift findByNameIgnoreCase(String name) {
        String sql = "select * from " + StringValue.tbl_shift + " where lower(name) = lower(?) limit 1";
        List<WorkShift> rows = jdbcTemplate.query(sql, new ShiftMapper(), name);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public WorkShift findByNameIgnoreCaseAndIdNot(String name, int excludedId) {
        String sql = "select * from " + StringValue.tbl_shift
                + " where lower(name) = lower(?) and id <> ? limit 1";
        List<WorkShift> rows = jdbcTemplate.query(sql, new ShiftMapper(), name, excludedId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    @Override
    public List<WorkShift> search(String key) {
        if (key == null || key.isBlank()) {
            return getAll();
        }

        String sql = "select * from " + StringValue.tbl_shift
                + " where lower(name) like lower(?) order by start_time asc, id asc";
        return jdbcTemplate.query(sql, new ShiftMapper(), "%" + key.trim() + "%");
    }

    @Override
    public int create(WorkShift item) {
        String sql = "insert into " + StringValue.tbl_shift + " (name, start_time, end_time, created_at) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, item.getName());
            statement.setObject(2, item.getStartTime());
            statement.setObject(3, item.getEndTime());
            statement.setObject(4, item.getCreatedAt());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return key == null ? 0 : key.intValue();
    }

    @Override
    public int update(WorkShift item) {
        if (item == null || item.getId() <= 0) {
            return 0;
        }

        String sql = "update " + StringValue.tbl_shift + " set name = ?, start_time = ?, end_time = ? where id = ?";
        return jdbcTemplate.update(sql, item.getName(), item.getStartTime(), item.getEndTime(), item.getId());
    }

    @Override
    public int delete(int id) {
        String sql = "delete from " + StringValue.tbl_shift + " where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
