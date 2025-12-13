package CinemaBooking.Group2.mappers;

import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.models.Combo;

public class ComboMapper {
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
}
