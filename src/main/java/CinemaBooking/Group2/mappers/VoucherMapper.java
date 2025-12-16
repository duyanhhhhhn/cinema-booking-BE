package CinemaBooking.Group2.mappers;

import CinemaBooking.Group2.dtos.concession.VoucherResponseDTO;
import CinemaBooking.Group2.models.Voucher;

public class VoucherMapper {
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
}
