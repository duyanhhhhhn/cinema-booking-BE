package CinemaBooking.Group2.controllers.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	@CrossOrigin
	public ResponseEntity<List<ComboResponseDTO>> getCombo(){
		List<ComboResponseDTO> item=service.getCombo();
		return ResponseEntity.ok(item);
	}
	@GetMapping("/combo/{id}")
	@CrossOrigin
	public ResponseEntity<ComboResponseDTO> comboInfo(@PathVariable("id")int id){
		ComboResponseDTO item=service.comboInfo(id);
		return ResponseEntity.ok(item);
	}
	@GetMapping("/products")
	@CrossOrigin
	public ResponseEntity<List<ProductResponseDTO>> getProduct(){
		List<ProductResponseDTO> item = pro.getProducts();
		return ResponseEntity.ok(item);
	}
	@GetMapping("/products/{id}")
	@CrossOrigin
	public ResponseEntity<ProductResponseDTO> productInfo(@PathVariable("id") int id){
		ProductResponseDTO item = pro.productInfo(id);
		return ResponseEntity.ok(item);
	}
}
