package CinemaBooking.Group2.controllers.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.concession.VoucherResponseDTO;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.models.Enum.DiscountType;
import CinemaBooking.Group2.service.MarketingService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class MarketingAdminController {
	@Autowired
	private MarketingService service;
	@GetMapping("/admin/api/vouchers")
	@CrossOrigin
	public ResponseEntity<List<VoucherResponseDTO>> getVoucher(){
		List<VoucherResponseDTO> item=null;
		try {
			item= service.getVoucher();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.ok(item);
		}
	@GetMapping("/api/vouchers/check")
	@CrossOrigin
	public ResponseEntity<BigDecimal> checkVoucher(@RequestParam("id") int id,@RequestParam("price")BigDecimal price) {
		BigDecimal rs = BigDecimal.valueOf(0);
		try {
			 rs = service.checkDiscount(id, price);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.ok(rs);
	}
	@PostMapping("/admin/api/vouchers")
	@CrossOrigin
	public ResponseEntity<String> addVoucher(@RequestParam("code") String code,
			@RequestParam("description") String description,@RequestParam("discount_type") DiscountType type,
			@RequestParam("discount_value") BigDecimal value,@RequestParam("min_order_amount") BigDecimal min_order,
			@RequestParam("start_at") LocalDate start, @RequestParam("end_at") LocalDate end,
			@RequestParam("usage_limit") int limit,@RequestParam("usage") int count){
		String ms="Failed";
		Voucher item = new Voucher();
		item.setCode(code);
		item.setDescription(description);
		item.setDiscountType(type);
		item.setDiscountValue(value);
		item.setMinOrderAmount(min_order);
		item.setStartAt(start);
		item.setEndAt(end);
		item.setUsageLimit(limit);
		item.setUsedCount(count);
		item.setCreatedAt(LocalDate.now());
		try {
			ms = service.addVoucher(item);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.ok(ms);
	}
}
