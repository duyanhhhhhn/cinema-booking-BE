package CinemaBooking.Group2.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.WorkShift;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class ShiftRepository implements Icrud<WorkShift>{
	@Autowired
	private JdbcTemplate db;
	public ShiftRepository() {
		
	}
	public class ShiftMapper implements RowMapper<WorkShift>{

		@Override
		public WorkShift mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			WorkShift item = new WorkShift();
			item.setId(rs.getInt("id"));
			item.setName(rs.getString("name"));
			item.setStartTime(rs.getTime("start_time").toLocalTime());
			item.setEndTime(rs.getTime("end_time").toLocalTime());
			item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
			return item;
		}
		
	}
	@Override
	public List<WorkShift> getAll() {
		// TODO Auto-generated method stub
		List<WorkShift> list = new ArrayList<>();
		try {
			list = db.query("select * from "+ StringValue.tbl_shift, new ShiftMapper());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return list;
	}
	

	@Override
	public WorkShift findById(int id) {
		// TODO Auto-generated method stub
		WorkShift item = new WorkShift();
		try {
			item = db.query("select * from "+ StringValue.tbl_shift+" where id=?", 
					new ShiftMapper(),new Object[] {id}).get(0);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return item;
	}

	@Override
	public List<WorkShift> search(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(WorkShift item) {
		// TODO Auto-generated method stub
		try {
			int rs = db.update("insert into "+ StringValue.tbl_shift+"(name,start_time,end_time,created_at) values(?,?,?,?)",
					new Object[] {item.getName(),item.getStartTime(),item.getEndTime(),item.getCreatedAt()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}

	@Override
	public int update(WorkShift item) {
		// TODO Auto-generated method stub
		try {
			int rs = db.update("update from "+ StringValue.tbl_shift+" set name=?,start_time=?,end_time=? where id=?",
					new Object[] {item.getName(),item.getStartTime(),item.getEndTime(),item.getId()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}

	@Override
	public int delete(int id) {
		// TODO Auto-generated method stub
		try {
			int rs = db.update("delete from "+ StringValue.tbl_shift+" where id=?",new Object[] {id});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	
}
