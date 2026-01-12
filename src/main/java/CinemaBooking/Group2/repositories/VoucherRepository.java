package CinemaBooking.Group2.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.VoucherMapper;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.models.Enum.DiscountType;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class VoucherRepository implements Icrud<Voucher>{
	@Autowired
	private JdbcTemplate db;
	public VoucherRepository() {
	}
	@Override
	public List<Voucher> getAll() {
		// TODO Auto-generated method stub
		try {
			List<Voucher> item =  db.query("select * from "+StringValue.tbl_voucher, new VoucherMapper());
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
			Voucher item = db.query("select * from "+StringValue.tbl_voucher+" where id=?", new VoucherMapper()
					,new Object[] {id}).get(0);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public Voucher findByCode(String code) {
		try {
			List<Voucher> items = db.query("select * from "+StringValue.tbl_voucher+" where code=?", new VoucherMapper()
					,new Object[] {code});
			if(items != null && !items.isEmpty()) {
				return items.get(0);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
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
			int rs =  db.update("insert into "+StringValue.tbl_voucher+"(code ,description,discount_type,discount_value,"
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