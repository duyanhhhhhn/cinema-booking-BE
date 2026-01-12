package CinemaBooking.Group2.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import CinemaBooking.Group2.mappers.StaffMapper;
import CinemaBooking.Group2.models.DbConnection;
import CinemaBooking.Group2.models.StaffSchedule;

public class ScheduleRepository implements Icrud<StaffSchedule>{
	private static ScheduleRepository _instance=null;
	private JdbcTemplate db;
	private ScheduleRepository() {
		db = DbConnection.Instance().getDb();
	}
	public static ScheduleRepository Instance() {
		if(_instance==null) {
			_instance=new ScheduleRepository();
		}
		return _instance;
	}
	public int assignSchedule(StaffSchedule item) {
		try {
			int rs = db.update("insert into `staff_schedule`(staff_id,shift_id,work_date,status,created_at) values(?,?,?,?,?)"
					,new Object[] {item.getStaffId(),item.getShiftId(),item.getWorkDate(),item.getStatus().name(),LocalDate.now()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}
	@Override
	public List<StaffSchedule> getAll() {
		// TODO Auto-generated method stub
		List<StaffSchedule> list = new ArrayList<>();
		try {
			list = db.query("select * from `staff_schedule`", new StaffMapper());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return list;
	}

	@Override
	public StaffSchedule findById(int id) {
		// TODO Auto-generated method stub
				StaffSchedule item = new StaffSchedule();
				try {
					item = db.query("select * from `staff_schedule` where id=?",
							new StaffMapper(),new Object[] {id}).get(0);
				}
				catch (Exception e) {
					// TODO: handle exception
					System.out.print(e.getMessage());
				}
				return item;
	}
	public List<StaffSchedule> getByStaffId(int staff_id){
		List<StaffSchedule> item = new ArrayList<>();
		try {
			item = db.query("select * from `staff_schedule` where staff_id=?",
					new StaffMapper(),new Object[] {staff_id});
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	public List<StaffSchedule> getByShift(int shift_id){
		List<StaffSchedule> item = new ArrayList<>();
		try {
			item = db.query("select * from `staff_schedule` where shift_id=?",
					new StaffMapper(),new Object[] {shift_id});
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}

	@Override
	public List<StaffSchedule> search(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(StaffSchedule item) {
		// TODO Auto-generated method stub
		try {
			int rs = db.update("insert into `staff_schedule`(staff_id,shift_id,work_date,status,created_at) values(?,?,?,?,?)"
					,new Object[] {item.getStaffId(),item.getShiftId(),item.getWorkDate(),item.getStatus(),item.getCreatedAt()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}

	@Override
	public int update(StaffSchedule item) {
		// TODO Auto-generated method stub
		try {
			if(item==null||findById(item.getId())==null) {
				return 0;
			}
			int rs = db.update("update `staff_schedule` set staff_id=?,shift_id=?,work_date=?,status=? where id=?",
					new Object[] {item.getStaffId(),item.getShiftId(),item.getWorkDate(),item.getStatus(),item.getId()});
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
		int rs=0;
		try {
			if(findById(id)!=null) {
				rs = db.update("delete from `staff_schedule` where id=?",new Object[] {id});
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return rs;
	}
	
	
}
