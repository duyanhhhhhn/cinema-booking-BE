package CinemaBooking.Group2.controllers.admin;

import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController

public class ProductController {
	@Autowired
	ProductService service;
	@PostMapping("/api/products")
	public String postMethodName(@RequestParam("name")String name
			,@RequestParam("description") String description,@RequestParam("price")BigDecimal price,
			@RequestParam("image")String image,@RequestParam("stock")int stock,@RequestParam("is_active")int active) {
		//TODO: process POST request
		Product item = new Product();
		item.setName(name);
		item.setDescription(description);
		item.setPrice(price);
		item.setImageUrl(image);
		item.setStock(stock);
		item.setIsActive(active);
		item.setCreatedAt(LocalDateTime.now());
		String a=service.createProduct(item);
		return a;
	}

}
