package CinemaBooking.Group2.controllers.admin;

import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController

public class ProductController {
	@Autowired
	ProductService service;
	

}
