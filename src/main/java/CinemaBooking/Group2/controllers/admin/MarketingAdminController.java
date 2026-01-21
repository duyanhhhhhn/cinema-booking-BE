package CinemaBooking.Group2.controllers.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.concession.VoucherResponseDTO;
import CinemaBooking.Group2.dtos.marketing.ListPostResponseDTO;
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
	@GetMapping("/admin/api/posts")
	@CrossOrigin
	public ResponseEntity<ListPostResponseDTO> getPost(){
		ListPostResponseDTO item = service.getAllPost();
		try {
			
			if(item.isIs_success()==false) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(item);
			}
			else {
				return ResponseEntity.ok(item);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(item); 
	}
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
	public ResponseEntity<VoucherResponseDTO> checkVoucher(@RequestParam("id") int id,@RequestParam("price")BigDecimal price) {
		VoucherResponseDTO rs = new VoucherResponseDTO();
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
	public ResponseEntity<VoucherResponseDTO> addVoucher(@RequestParam("code") String code,
			@RequestParam("description") String description,@RequestParam("discount_type") DiscountType type,
			@RequestParam("discount_value") BigDecimal value,@RequestParam("min_order_amount") BigDecimal min_order,
			@RequestParam("start_at") LocalDate start, @RequestParam("end_at") LocalDate end,
			@RequestParam("usage_limit") int limit,@RequestParam("usage") int count){
		VoucherResponseDTO dto = new VoucherResponseDTO();
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
			dto = service.addVoucher(item);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.ok(dto);
	}
}
