package CinemaBooking.Group2.mappers;

import CinemaBooking.Group2.dtos.concession.ProductResponseDTO;
import CinemaBooking.Group2.models.Product;

public class ProductMapper {
	public static ProductResponseDTO toResponseDTO(Product item) {
		if(item==null) {
			return null;
		};
		try {
			ProductResponseDTO res = new ProductResponseDTO();
			res.setId(item.getId());
			res.setName(item.getName());
			res.setDescription(item.getDescription());
			res.setCreatedAt(item.getCreatedAt());
			res.setIsActive(item.getIsActive());
			res.setStock(item.getStock());
			res.setImageUrl(item.getImageUrl());
			res.setPrice(item.getPrice());
			return res;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return null;
	}
}
