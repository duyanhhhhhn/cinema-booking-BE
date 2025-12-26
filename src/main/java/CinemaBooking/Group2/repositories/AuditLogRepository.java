package CinemaBooking.Group2.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.models.AuditLog;
import CinemaBooking.Group2.models.DbConnection;

public class AuditLogRepository implements Icrud<AuditLog>{
	private static AuditLogRepository _instance;
	private JdbcTemplate db;
	private AuditLogRepository () {
		db = DbConnection.Instance().getDb();
	}
	public static AuditLogRepository Instance() {
		if(_instance==null) {
			_instance = new AuditLogRepository();
		}
		return _instance;
	}
	public class AuditLogMapper implements RowMapper<AuditLog>{

		@Override
		public AuditLog mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			AuditLog item = new AuditLog();
			item.setId(rs.getInt("id"));
			item.setUserId(rs.getInt("user_id"));
			item.setAction(rs.getString("action"));
			item.setResourceId(rs.getString("resource_id"));
			item.setResourceType(rs.getString("resource_type"));
			item.setDetails(rs.getString("details"));
			item.setIpAddress(rs.getString("ip_address"));
			item.setUserAgent(rs.getString("user_agent"));
			item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
			return item;
		}
		
	}
	@Override
	public List<AuditLog> getAll() {
		// TODO Auto-generated method stub
		List<AuditLog> item = new ArrayList<>();
		try {
			item = db.query("select * from `audit_log`", new AuditLogMapper());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return item;
	}

	@Override
	public AuditLog findById(int id) {
		// TODO Auto-generated method stub
		AuditLog item = new AuditLog();
		try {
			item = db.query("select * from `audit_log` where id=?",
					new AuditLogMapper(),new Object[] {id}).get(0);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}

	@Override
	public List<AuditLog> search(String key) {
		// TODO Auto-generated method stub
		List<AuditLog> list = new ArrayList<>();
		try {
			list = db.query("select * from `audit_log` where name",
					new AuditLogMapper());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	@Override
	public int create(AuditLog item) {
		// TODO Auto-generated method stub
		try {
			int rs = db.update("insert into `audit_log`"
					+ "(user_id,action,resource_id,resouce_type,details,ip_address,user_agent,created_at) values("
					+ "?,?,?,?,?,?,?,?)",new Object[] {item.getUserId(),item.getAction(),item.getResourceId(),
							item.getResourceType(),item.getDetails(),item.getIpAddress(),item.getUserAgent(),
							item.getCreatedAt()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}

	@Override
	public int update(AuditLog item) {
		// TODO Auto-generated method stub
		try {
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
			int rs = db.update("delete from `audit log` where id=?",
					new Object[] {id});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}
}
