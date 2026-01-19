package CinemaBooking.Group2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.home.BannerCRUDResponseDTO;
import CinemaBooking.Group2.dtos.home.BannerListResponseDTO;
import CinemaBooking.Group2.dtos.home.BannerResponseDTO;
import CinemaBooking.Group2.mappers.BannerMapper;
import CinemaBooking.Group2.models.Banner;
import CinemaBooking.Group2.models.Banner.BannerPosition;
import CinemaBooking.Group2.pattern.Home;

@Service
public class BannerService {
	@Autowired
	private Home home;
	public List<BannerResponseDTO> getBanner(){
		try {
			List<Banner> item = home.getBanner();
			if(item==null) {
				item = new ArrayList<>();
			}
			return item.stream().map(BannerMapper::toResponseDTO).collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public BannerListResponseDTO getBanner(String url){
		try {
			List<Banner> item = home.getBannerByLinkURL(url);
			if(item==null) {
				item = new ArrayList<>();
			}
			BannerListResponseDTO banner = new BannerListResponseDTO();
			banner.setData(item);
			return banner;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public BannerListResponseDTO getBannerByPosition(BannerPosition position,int count){
		try {
			List<Banner> item = home.findBannerByPosition(position, count);
			if(item==null) {
				item = new ArrayList<>();
			}
			BannerListResponseDTO banner = new BannerListResponseDTO();
			banner.setData(item);
			return banner;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public BannerResponseDTO getBannerById(int id) {
		BannerResponseDTO banner=null;
		try {
			Banner item = home.getBannerById(id);
			if(item==null) {
				banner = new BannerResponseDTO();
				banner.setMessage("Error");
				banner.setStatus(false);
			}
			else {
				banner = BannerMapper.toResponseDTO(item);
				banner.setMessage("success");
				banner.setStatus(true);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return banner;
	}
	public BannerCRUDResponseDTO addBanner(Banner banner) {
		BannerCRUDResponseDTO res = new BannerCRUDResponseDTO();
		try {
			int rs = home.AddBanner(banner);
			if(rs==0) {
				res.setIs_active(false);
				res.setMessage("Error");
			}
			else {
				res.setIs_active(true);
				res.setMessage("Created");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}
}
