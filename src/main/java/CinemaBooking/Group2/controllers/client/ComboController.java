package CinemaBooking.Group2.controllers.client;

import java.util.ArrayList;
import java.util.List;

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

import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.concession.ComboListResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.dtos.concession.ProductResponseDTO;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.service.ComboService;
import CinemaBooking.Group2.service.ProductService;
import CinemaBooking.Group2.ultis.StringValue;

@RestController
@RequestMapping("/api/concessions")
public class ComboController {

	@Autowired
	private ComboService service;
	@Autowired
	private ProductService pro;
	@GetMapping("/public/combo")
	@CrossOrigin
	public ResponseEntity<PageResponse<Combo>> getCombo(
			@RequestParam("page")int page,@RequestParam("size")int size) {
		PageResponse<Combo> response = new PageResponse<>();
		// String message = "Error";
		try {
			float totalItem = service.getCombo().size();
			float totalPage = StringValue.calculateTotalPage(totalItem, size);
			List<Combo> item = new ArrayList<>();
			item = service.getCombo(page,size);
			if (item == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			} else {
				response.setItems(item);
				response.setTotalItems((long)totalItem);
				response.setTotalPages((long)totalPage);
				response.setPage(page);
				response.setSize(size);
				response.setMessage("Success");
				response.setSuccess(true);
				return ResponseEntity.ok(response);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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

	public ResponseEntity<PageResponse<Product>> getProduct(@RequestParam("page") int page,
			@RequestParam("size") int size) {
		PageResponse<Product> res = new PageResponse<>();
		try {
			List<Product> item = null;
			float totalItem = pro.getProducts().size();
			float totalPage = StringValue.calculateTotalPage(totalItem, size);
			if (totalPage < page) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
			}
			item = pro.getProducts(page, size);
			res.setSize(size);
			res.setPage(page);
			res.setSuccess(true);
			res.setItems(item);
			res.setTotalItems((int) totalItem);
			res.setTotalPages((long)totalPage);
			res.setMessage("Success");
			return ResponseEntity.ok(res);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
	}

	@GetMapping("/products/{id}")
	@CrossOrigin
	public ResponseEntity<ProductResponseDTO> productInfo(@PathVariable("id") int id) {
		ProductResponseDTO item = pro.productInfo(id);
		return ResponseEntity.ok(item);
	}
}
