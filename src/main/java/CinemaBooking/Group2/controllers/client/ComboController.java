package CinemaBooking.Group2.controllers.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	@GetMapping("/combo")
	public ResponseEntity<List<ComboResponseDTO>> getCombo(){
		List<ComboResponseDTO> item=service.getCombo();
		return ResponseEntity.ok(item);
	}
	@GetMapping("/combo/{id}")
	public ResponseEntity<ComboResponseDTO> comboInfo(@PathVariable("id")int id){
		ComboResponseDTO item=service.comboInfo(id);
		return ResponseEntity.ok(item);
	}
	@GetMapping("/products")
	public ResponseEntity<List<ProductResponseDTO>> getProduct(){
		List<ProductResponseDTO> item = pro.getProducts();
		return ResponseEntity.ok(item);
	}
	@GetMapping("/products/{id}")
	public ResponseEntity<List<ProductResponseDTO>> productInfo(@PathVariable("id") int id){
		List<ProductResponseDTO> item = pro.productInfo(id);
		return ResponseEntity.ok(item);
	}
}
