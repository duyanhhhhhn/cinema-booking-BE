package CinemaBooking.Group2.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.concession.VoucherResponseDTO;
import CinemaBooking.Group2.service.VoucherService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class MarketingAdminController {
	@Autowired
	private VoucherService vouchService;
	@GetMapping("/admin/api/vouchers")
	public ResponseEntity<List<VoucherResponseDTO>> getVoucher(){
		List<VoucherResponseDTO> item=null;
		try {
			item= vouchService.getVoucher();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.ok(item);
	}
}
