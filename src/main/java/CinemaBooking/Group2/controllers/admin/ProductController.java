package CinemaBooking.Group2.controllers.admin;

import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController

public class ProductController {
	@Autowired
	ProductService service;
	@PostMapping("/api/products")
	public String postMethodName(@RequestBody Product entity) {
		//TODO: process POST request
		String a=service.createProduct(entity);
		return a;
	}

}
