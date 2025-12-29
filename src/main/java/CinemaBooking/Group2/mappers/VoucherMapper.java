package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.dtos.concession.VoucherResponseDTO;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.models.Enum.DiscountType;

public class VoucherMapper implements RowMapper<Voucher>{
	public static VoucherResponseDTO toResponseDTO(Voucher item) {
		if(item==null) {
			return null;
		}
		VoucherResponseDTO res = new VoucherResponseDTO();
		res.setCode(item.getCode());
		res.setDescription(item.getDescription());
		res.setCreatedAt(item.getCreatedAt());
		res.setDiscountType(item.getDiscountType());
		res.setDiscountValue(item.getDiscountValue());
		res.setStartAt(item.getStartAt());
		res.setEndAt(item.getEndAt());
		res.setMinOrderAmount(item.getMinOrderAmount());
		res.setUsageLimit(item.getUsageLimit());
		res.setId(item.getId());
		return res;
	}
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
