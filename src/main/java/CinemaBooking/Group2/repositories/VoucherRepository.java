package CinemaBooking.Group2.repositories;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.models.DbConnection;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.models.Enum.DiscountType;

public class VoucherRepository implements Icrud<Voucher>{
	private JdbcTemplate db;
	private VoucherRepository() {
		db=DbConnection.Instance().getDb();
	}
	private static VoucherRepository _instance=null;
	public static VoucherRepository Instance() {
		if(_instance==null) {
			_instance= new VoucherRepository();
		}
		return _instance;
	}
	
	class VoucherMapper implements RowMapper<Voucher>{

		@Override
		public Voucher mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			Voucher item = new Voucher();
			item.setId(rs.getInt("id"));
			item.setCode(rs.getString("code"));
			item.setDescription(rs.getString("description"));
			item.setDiscountType(DiscountType.valueOf(rs.getString("discount_type")));
			item.setDiscountValue(rs.getBigDecimal("discount_value"));
			item.setMinOrderAmount(rs.getBigDecimal("min_order_amount"));
			item.setUsageLimit(rs.getInt("usage_limit"));
			item.setUsedCount(rs.getInt("used_count"));
			item.setStartAt(rs.getDate("start_at").toLocalDate());
			item.setCreatedAt(rs.getDate("created_at").toLocalDate());
			item.setEndAt(rs.getDate("end_at").toLocalDate());
			return item;
		}
	}

	@Override
	public List<Voucher> getAll() {
		// TODO Auto-generated method stub
		try {
			List<Voucher> item =  db.query("select * from `vouchers`", new VoucherMapper());
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return null;
	}

	@Override
	public Voucher findById(int id) {
		// TODO Auto-generated method stub
		try {
			Voucher item = db.query("select * from `vouchers` where id=?", new VoucherMapper()
					,new Object[] {id}).get(0);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@Override
	public List<Voucher> search(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Voucher item) {
		// TODO Auto-generated method stub
		try {
			int rs =  db.update("insert into `vouchers`(code ,description,discount_type,discount_value,"
					+ "min_order_amount,start_at,end_at,usage_limit,used_count,created_at) "
					+ "values(?,?,?,?,?,?,?,?,?,?)",new Object[] {item.getCode(),item.getDescription(),
							item.getDiscountType().name(),item.getDiscountValue(),item.getMinOrderAmount(),
							item.getStartAt(),item.getEndAt(),item.getUsageLimit(),item.getUsedCount(),
							item.getCreatedAt()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return 0;
	}

	@Override
	public int update(Voucher item) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(int id) {
		// TODO Auto-generated method stub
		return 0;
	}
	public boolean checkAvailableVoucher(int id) {
		try {
			Voucher item = findById(id);
			LocalDate date = LocalDate.now();
			if(item!=null) {
				if(date.isAfter(item.getStartAt())&&
						date.isBefore(item.getEndAt())) {
					if(item.getUsageLimit()>item.getUsedCount()) {
						return true;
					}
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return false;
	}
	public BigDecimal checkDiscount(int id,BigDecimal price) {
		BigDecimal rs = BigDecimal.valueOf(0);
		try {
			if(checkAvailableVoucher(id)) {
				Voucher item =findById(id);
				if(price.compareTo(item.getDiscountValue())>0) {
					if(item.getDiscountType()==DiscountType.AMOUNT) {
							rs = price.subtract(item.getDiscountValue());
							return rs;
					}
					else if(item.getDiscountType()==DiscountType.PERCENT){
						rs = price.multiply(item.getDiscountValue()).divide(BigDecimal.valueOf(100));
						return rs;
					}
				}
				else {
					return rs;
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
}
