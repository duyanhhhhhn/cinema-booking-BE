package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.models.Payment;
import CinemaBooking.Group2.models.Enum.PaymentMethod;
import CinemaBooking.Group2.models.Enum.PaymentStatus;

public class PaymentMapper implements RowMapper<Payment>{

	@Override
	public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Payment item = new Payment();
		item.setId(rs.getInt("id"));
		item.setAmount(rs.getBigDecimal("amount"));
		item.setBookingId(rs.getInt("booking_id"));
		item.setMethod(PaymentMethod.valueOf(rs.getString("method")));
		item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		item.setStatus(PaymentStatus.valueOf(rs.getString("status")));
		item.setPaidAt(rs.getTimestamp("paid_at").toLocalDateTime());
		item.setProviderPaymentId(rs.getString("provider_payment_id"));
		return item;
	}
	

}
