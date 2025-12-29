package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.ProductResponseDTO;
import CinemaBooking.Group2.mappers.ProductMapper;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.pattern.Concessions;

@Service
public class ProductService {
	public List<ProductResponseDTO> getProducts (){
		try{
			List<Product> item = Concessions.Instance().getProduct();
			return item.stream().map(ProductMapper::toResponseDTO)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
	public ProductResponseDTO productInfo (int id){
		try{
			Product item = Concessions.Instance().productInfo(id);
			return ProductMapper.toResponseDTO(item);
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
	public String createProduct(Product item) {
		String s = "Failed to create a new product";
		try {
			int rs = Concessions.Instance().createProduct(item);
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
