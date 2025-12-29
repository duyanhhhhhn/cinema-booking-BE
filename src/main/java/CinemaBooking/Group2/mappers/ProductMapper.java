package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.dtos.concession.ProductResponseDTO;
import CinemaBooking.Group2.models.Product;

public class ProductMapper implements RowMapper<Product>{
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
	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Product pro = new Product();
		pro.setId(rs.getInt("id"));
		pro.setName(rs.getNString("name"));
		pro.setDescription(rs.getNString("description"));
		pro.setPrice(rs.getBigDecimal("price"));
		pro.setStock(rs.getInt("stock"));
		pro.setIsActive(rs.getInt("is_active"));
		pro.setImageUrl(rs.getNString("image_url"));
		return pro;
	}
}
