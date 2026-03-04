package CinemaBooking.Group2.controllers.admin;

import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;


@RestController

public class ProductController {
	@Autowired
	ProductService service;
	

}
