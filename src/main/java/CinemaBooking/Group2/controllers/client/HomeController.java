package CinemaBooking.Group2.controllers.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.home.BannerListResponseDTO;
import CinemaBooking.Group2.dtos.home.BannerResponseDTO;
import CinemaBooking.Group2.service.BannerService;

@RestController
@RequestMapping("/api/home")
public class HomeController {
	@Autowired
	private BannerService service;
	@GetMapping("/banner")
	public ResponseEntity<BannerResponseDTO> getBannerById(@RequestParam("id") int id){
		BannerResponseDTO banner = new BannerResponseDTO();
		try {
			if(id==0) {
				banner.setMessage("Banner id shouldn't be 0");
				banner.setIsActive(false);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(banner);
			}
			BannerResponseDTO item = service.getBannerById(id);
			if(item!=null) {
				banner = item;
				banner.setMessage("Success");
				banner.setStatus(true);
				return ResponseEntity.ok(banner);
			}
			else if(item==null) {
				banner.setMessage("Not Found");
				banner.setStatus(false);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(banner);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(banner);
	}
	@GetMapping("/banner/url")
	public ResponseEntity<BannerListResponseDTO> getBannerByURL(@RequestParam("url")String url){
		BannerListResponseDTO list = new BannerListResponseDTO();
		try {
			if(url==null||url.isEmpty()) {
				list.setIs_success(false);
				list.setMessage("URL shouldn't be empty or null !");
				return ResponseEntity.ok(list);
			}
			else {
				BannerListResponseDTO banner = service.getBanner(url);
				if(banner==null) {
					list.setMessage("Not found !");
					list.setIs_success(false);
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(list);
				}
				list.setBanners(banner.getBanners());
				list.setMessage("Success");
				list.setIs_success(true);
				return ResponseEntity.ok(list);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(list);
	}
}
