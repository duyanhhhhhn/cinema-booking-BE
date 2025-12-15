package CinemaBooking.Group2.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.models.Voucher.DiscountType;

@Repository
public class VoucherRepository {
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
}
