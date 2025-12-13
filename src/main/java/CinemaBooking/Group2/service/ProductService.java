package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.ProductResponseDTO;
import CinemaBooking.Group2.mappers.ProductMapper;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.repositories.ProductRepository;

@Service
public class ProductService {
	@Autowired
	ProductRepository rep;
	public List<ProductResponseDTO> getProducts (){
		try{
			List<Product> item = rep.getAll();
			return item.stream().map(ProductMapper::toResponseDTO)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
	public List<ProductResponseDTO> productInfo (int id){
		try{
			List<Product> item = rep.findById(id);
			return item.stream().map(ProductMapper::toResponseDTO)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
	public String createProduct(Product item) {
		String s = "Failed to create a new product";
		try {
			int rs = rep.CreateProduct(item);
			if(rs==1) {
				s = "Success";
			}
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return s;
	}
}
