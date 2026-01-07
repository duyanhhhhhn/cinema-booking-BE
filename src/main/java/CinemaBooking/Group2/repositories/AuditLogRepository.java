package CinemaBooking.Group2.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.AuditLogMapper;
import CinemaBooking.Group2.models.AuditLog;

@Repository
public class AuditLogRepository implements Icrud<AuditLog>{
	@Autowired
	private JdbcTemplate db;
	public AuditLogRepository () {
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
