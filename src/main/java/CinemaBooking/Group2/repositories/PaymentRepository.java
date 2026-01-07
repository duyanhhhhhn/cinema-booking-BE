package CinemaBooking.Group2.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import CinemaBooking.Group2.mappers.PaymentMapper;
import CinemaBooking.Group2.models.DbConnection;
import CinemaBooking.Group2.models.Payment;

public class PaymentRepository implements Icrud<Payment>{
	private static PaymentRepository _instance=null;
	private JdbcTemplate db;
	private PaymentRepository() {
		db = DbConnection.Instance().getDb();
	}
	public static PaymentRepository Instance() {
		if(_instance==null) {
			_instance = new PaymentRepository();
		}
		return _instance;
	}
	@Override
	public List<Payment> getAll() {
		// TODO Auto-generated method stub
		try {
			List<Payment> item = new ArrayList<>();
			item = db.query("select * from `payments`", new PaymentMapper());
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	@Override
	public Payment findById(int id) {
		try {
			Payment item = new Payment();
			item = db.query("select * from `payments`", new PaymentMapper()).get(0);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	@Override
	public List<Payment> search(String key) {
		// TODO Auto-generated method stub
		try {
			List<Payment> item = new ArrayList<>();
			item = db.query("select * from `payments` where like ?", new PaymentMapper(),new Object[] {key});
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	@Override
	public int create(Payment item) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int update(Payment item) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int delete(int id) {
		// TODO Auto-generated method stub
		return 0;
	}
	public BigDecimal getRevenueByMonth(int month) {
		BigDecimal rs = new BigDecimal(0);
		try {
			List<Payment> list = db.query("select * from `payments` where month(paid_at)=?", 
					new PaymentMapper(),new Object[] {month});
			for(int i=0;i<list.size();i++) {
				rs=rs.add(list.get(i).getAmount());
			}
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return BigDecimal.valueOf(0);
	}
	public BigDecimal getRevenueByDay(LocalDate date) {
		BigDecimal rs = new BigDecimal(0);
		try {
			List<Payment> list = db.query("select * from `payments` where paid_at=?", 
					new PaymentMapper(),new Object[] {date});
			for(int i=0;i<list.size();i++) {
				rs=rs.add(list.get(i).getAmount());
			}
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return BigDecimal.valueOf(0);
	}
}
