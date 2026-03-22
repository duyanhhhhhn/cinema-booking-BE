package CinemaBooking.Group2.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.StaffMapper;
import CinemaBooking.Group2.mappers.UserMapper;
import CinemaBooking.Group2.models.StaffSchedule;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class ScheduleRepository implements Icrud<StaffSchedule>{
	@Autowired
	private JdbcTemplate db;
	public ScheduleRepository() {
		
	}
	public int assignSchedule(StaffSchedule item) {
		try {
			int rs = db.update("insert into "+StringValue.tbl_schedule+"(staff_id,shift_id,work_date,status,created_at) values(?,?,?,?,?)"
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
			list = db.query("select * from "+StringValue.tbl_schedule, new StaffMapper());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return list;
	}
	public List<StaffSchedule> getAll(int page,int size){
		List<StaffSchedule> list = new ArrayList<>();
		try {
			int i = (page-1)*size;
			list = db.query("select * from "+StringValue.tbl_schedule+ " limit ? offset ?", new StaffMapper(),new Object[] {size,i});
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return list;
	}
	public StaffSchedule getSchedulesByDate(LocalDate date){
		StaffSchedule list = new StaffSchedule();
		try {
			list = db.query("select * from "+StringValue.tbl_schedule+" where work_date = ?", new StaffMapper(),new Object[] {date}).get(0);
			return list;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	public List<StaffSchedule> getShedulesByRange(LocalDate startDate, LocalDate endDate) {
		List<StaffSchedule> list = new ArrayList<>();
		try {
			list = db.query("select * from "+StringValue.tbl_schedule+
					" where work_date between ? and ? order by staff_id",
					new StaffMapper(),new Object[] {startDate,endDate});
			return list;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	@Override
	public StaffSchedule findById(int id) {
		// TODO Auto-generated method stub
				StaffSchedule item = new StaffSchedule();
				try {
					item = db.query("select * from "+StringValue.tbl_schedule+" where id=?",
							new StaffMapper(),new Object[] {id}).get(0);
				}
				catch (Exception e) {
					// TODO: handle exception
					System.out.print(e.getMessage());
				}
				return item;
	}
	public List<StaffSchedule> getByStaffId(int staff_id,LocalDate startDate,LocalDate endDate){
		List<StaffSchedule> item = new ArrayList<>();
		try {
			item = db.query("select * from "+StringValue.tbl_schedule+" where staff_id=?"
					+ " and work_date between ? and ?",
					new StaffMapper(),new Object[] {staff_id,startDate,endDate});
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return item;
	}
	public List<StaffSchedule> getByShift(int shift_id){
		List<StaffSchedule> item = new ArrayList<>();
		try {
			item = db.query("select * from "+StringValue.tbl_schedule+" where shift_id=?",
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
			int rs = db.update("insert into "+StringValue.tbl_schedule+"(staff_id,shift_id,work_date,status,created_at) values(?,?,?,?,?)"
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
			int rs = db.update("update "+StringValue.tbl_schedule+" set staff_id=?,shift_id=?,work_date=?,status=? where id=?",
					new Object[] {item.getStaffId(),item.getShiftId(),item.getWorkDate(),item.getStatus(),item.getId()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}
	public List<User> getAllStaff() {
		try {
			List<User> staff = db.query("select u.*,r.name as role_name from user u inner join role r on u.role_id = r.id  where role_id=3",new UserMapper());
			return staff;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public User findStaffById(int id) {
		try {
			User staff = db.query("select u.*,r.name as role_name from user u inner join role r on u.role_id = r.id  where u.id = ?",new UserMapper(),new Object[] {id}).get(0);
			return staff;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}

	@Override
	public int delete(int id) {
		// TODO Auto-generated method stub
		int rs=0;
		try {
			if(findById(id)!=null) {
				rs = db.update("delete from "+StringValue.tbl_schedule+" where id=?",new Object[] {id});
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return rs;
	}
	
	
}
