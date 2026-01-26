package CinemaBooking.Group2.controllers.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.dtos.concession.ProductResponseDTO;
import CinemaBooking.Group2.service.ComboService;
import CinemaBooking.Group2.service.ProductService;
@RestController
@RequestMapping("/api/concessions")
public class ComboController {

	@Autowired
	private ComboService service;
	@Autowired
	private ProductService pro;
	@GetMapping("/public/combo")
	@CrossOrigin
	public ResponseEntity<ApiResponse<List<ComboResponseDTO>>> getCombo(
			@RequestParam("page")int page,@RequestParam("size")int size) {
		ApiResponse<List<ComboResponseDTO>> response;
		// String message = "Error";
		try {
			float totalItem = service.getCombo().size();
			Map<String, Object> meta = new HashMap<>();
			List<ComboResponseDTO> item = new ArrayList<>();
			item = service.getCombo(page,size);
			if (item == null) {
				response = new ApiResponse<List<ComboResponseDTO>>("Not found", null);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			} else {
				meta.put("perPage", size);
				meta.put("page",page);
				meta.put("total", totalItem);
				response = new ApiResponse<List<ComboResponseDTO>>("Success",item,meta);
				return ResponseEntity.ok(response);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

	@GetMapping("/public/combo/{id}")
	@CrossOrigin
	public ResponseEntity<ComboResponseDTO> comboInfo(@Validated @PathVariable("id") int id) {
		ComboResponseDTO item = null;
		try {
			item = service.comboInfo(id);
			if (item == null) {
				item = new ComboResponseDTO("We don't have any combo with this id", false);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(item);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}

		return ResponseEntity.ok(item);
	}

	@GetMapping("/products")
	@CrossOrigin
	public ResponseEntity<List<ProductResponseDTO>> getProduct() {
		List<ProductResponseDTO> item = null;
		try {
			item = pro.getProducts();
			if (item == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(item);
			}
			return ResponseEntity.ok(item);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(item);
	}

	public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getProduct(@RequestParam("page") int page,
			@RequestParam("size") int size) {
		ApiResponse<List<ProductResponseDTO>> res;
		try {
			List<ProductResponseDTO> item = null;
			float totalItem = pro.getProducts().size();
			item = pro.getProducts(page, size);
			Map<String, Object> meta = new HashMap<>();
			meta.put("perPage", size);
			meta.put("page",page);
			meta.put("total", totalItem);
			res = new ApiResponse<List<ProductResponseDTO>>("success", item);
			return ResponseEntity.ok(res);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

	@GetMapping("/products/{id}")
	@CrossOrigin
	public ResponseEntity<ProductResponseDTO> productInfo(@PathVariable("id") int id) {
		ProductResponseDTO item = pro.productInfo(id);
		return ResponseEntity.ok(item);
	}
}
