package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.dtos.home.BannerResponseDTO;
import CinemaBooking.Group2.models.Banner;
import CinemaBooking.Group2.models.Banner.BannerPosition;

public class BannerMapper implements RowMapper<Banner>{

	@Override
	public Banner mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Banner item = new Banner();
		item.setId(rs.getInt("id"));
		item.setTitle(rs.getString("title"));
		item.setImageUrl(rs.getString("image_url"));
		item.setLinkUrl(rs.getString("link_url"));
		item.setPosition(BannerPosition.valueOf(rs.getString("position")));
		item.setIsActive(rs.getBoolean("is_active"));
		item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		return item;
	}
	public static BannerResponseDTO toResponseDTO(Banner item) {
		try {
			if(item==null) {
				return null;
			}
			BannerResponseDTO banner = new BannerResponseDTO();
			banner.setId(item.getId());
			banner.setImageUrl(item.getImageUrl());
			banner.setLinkUrl(item.getLinkUrl());
			banner.setPosition(item.getPosition());
			banner.setTitle(item.getTitle());
			banner.setCreatedAt(item.getCreatedAt());
			return banner;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}
