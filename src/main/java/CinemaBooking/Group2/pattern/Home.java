package CinemaBooking.Group2.pattern;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.Banner;
import CinemaBooking.Group2.repositories.BannerRepository;

@Repository
public class Home {
	@Autowired
	private BannerRepository bannerRep;
	public Home() {
	}
	public List<Banner> getBanner() {
		try {
			return bannerRep.getAll();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public Banner getBannerById(int id) {
		try {
			return bannerRep.findById(id);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public List<Banner> getBannerByLinkURL(String url){
		try {
			return bannerRep.findByLinkURL(url);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public int AddBanner(Banner item) {
		try {
			return bannerRep.create(item);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public int changeBanner(Banner item) {
		try {
			return bannerRep.update(item);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public int deleteBanner(int id) {
		try {
			return bannerRep.delete(id);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
}
