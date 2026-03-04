package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.ComboItemResponseDTO;
import CinemaBooking.Group2.models.ComboItem;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.pattern.Concessions;

@Service
public class ComboItemMapper implements RowMapper<ComboItem> {
	@Autowired
	private Concessions con;
	
	@Override
	public ComboItem mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		ComboItem item = new ComboItem();
		item.setId(rs.getInt("id"));
		item.setComboId(rs.getInt("combo_id"));
		item.setProductId(rs.getInt("product_id"));
		item.setQuantity(rs.getInt("quantity"));
		return item;
	}
	
	public ComboItemResponseDTO toResponseDTO(ComboItem item) {
		if(item == null) {
			return null;
		}
		try {
			Product pro = con.productInfo(item.getProductId());
			if (pro == null) {
				return null;
			}
			
			// Use the new ComboItemResponseDTO structure with productId, productName, quantity
			ComboItemResponseDTO res = new ComboItemResponseDTO(
				item.getProductId(),
				pro.getName(),
				item.getQuantity(),
				pro.getPrice()
			);
			
			return res;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error in ComboItemMapper.toResponseDTO: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}