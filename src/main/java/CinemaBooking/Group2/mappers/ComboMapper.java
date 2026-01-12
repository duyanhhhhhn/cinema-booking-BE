package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.models.Combo;

public class ComboMapper implements RowMapper<Combo> {
	public static ComboResponseDTO toResponseDTO(Combo item) {
		if(item==null) {
			return null;
		};
		try {
			ComboResponseDTO res = new ComboResponseDTO();
			res.setId(item.getId());
			res.setName(item.getName());
			res.setDescription(item.getDescription());
			res.setPrice(item.getPrice());
			res.setCreatedAt(item.getCreatedAt());
			res.setIsActive(item.getIsActive());
			res.setImageUrl(item.getImageUrl());
			return res;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return null;
	}

	@Override
	public Combo mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Combo item = new Combo();
		item.setId(rs.getInt("id"));
		item.setName(rs.getNString("name"));
		item.setDescription(rs.getNString("description"));
		item.setPrice(rs.getBigDecimal("price"));
		item.setImageUrl(rs.getNString("image_url"));
		if(rs.getString("is_active")==null) {
			item.setIsActive(rs.getInt("is_active"));
		}
		item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		//item.setComboItems(rep.getByCombo(item.getId()));
		return item;
	}
}
