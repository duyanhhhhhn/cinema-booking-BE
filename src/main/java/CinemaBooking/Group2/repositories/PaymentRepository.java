package CinemaBooking.Group2.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.PaymentMapper;
import CinemaBooking.Group2.models.Payment;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class PaymentRepository implements Icrud<Payment>{
	@Autowired
	private JdbcTemplate db;
	public PaymentRepository() {
		
	}
	@Override
	public List<Payment> getAll() {
		// TODO Auto-generated method stub
		try {
			List<Payment> item = new ArrayList<>();
			item = db.query("select * from "+StringValue.tbl_payment, new PaymentMapper());
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
			item = db.query("select * from "+StringValue.tbl_payment+" where id=?",
					new PaymentMapper(),new Object[] {id}).get(0);
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
			item = db.query("select * from "+StringValue.tbl_payment+" where like ?", new PaymentMapper(),new Object[] {key});
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
			List<Payment> list = db.query("select * from "+StringValue.tbl_payment+" where month(paid_at)=?", 
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
			List<Payment> list = db.query("select * from "+StringValue.tbl_payment+" where paid_at=?", 
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
