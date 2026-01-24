package CinemaBooking.Group2.controllers.client;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.home.BannerListResponseDTO;
import CinemaBooking.Group2.dtos.home.BannerRequestDTO;
import CinemaBooking.Group2.dtos.home.BannerResponseDTO;
import CinemaBooking.Group2.models.Banner;
import CinemaBooking.Group2.models.Banner.BannerPosition;
import CinemaBooking.Group2.service.BannerService;
import CinemaBooking.Group2.ultis.FileUltility;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api")
public class HomeController {
	@Autowired
	private BannerService service;
	@GetMapping("/public/banner")
	public ResponseEntity<ApiResponse<List<BannerResponseDTO>>> getBanner(){
		ApiResponse<List<BannerResponseDTO>> res;
		try {
			List<BannerResponseDTO> item = service.getBanner();
			if(item!=null) {
				res = new ApiResponse<List<BannerResponseDTO>>("success", item);
				return ResponseEntity.ok(res);
			}
			else if(item==null) {
				res = new ApiResponse<List<BannerResponseDTO>>("not found", null);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}
	@GetMapping("/public/banner/{id}")
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
			System.out.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(banner);
	}
	@GetMapping("/banner/get")
	public ResponseEntity<BannerListResponseDTO> getBannerByPosition(@RequestParam("position")BannerPosition position,@RequestParam("count")int count){
		BannerListResponseDTO list = new BannerListResponseDTO();
		try {
			if(position==null) {
				list.setMessage("position shouldn't be empty!");
				return ResponseEntity.ok(list);
			}
			else {
				BannerListResponseDTO banner = service.getBannerByPosition(position, count);
				if(banner==null) {
					list.setMessage("Not found !");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(list);
				}
				list = banner;
				list.setMessage("Success");
				return ResponseEntity.ok(list);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(list);
	}
	@PostMapping("/public/banner")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<PageResponse<BannerResponseDTO>> addBanner(@RequestBody() BannerRequestDTO dto,@RequestParam("Image")MultipartFile image){
		PageResponse<BannerResponseDTO> res = new PageResponse<>();
		try {
			if(dto==null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
			}
			else {
				String url =FileUltility.uploadFileImage(image, "uploads/banner");
				System.out.print(url);
				Banner banner = new Banner();
				banner.setTitle(dto.getTitle());
				banner.setLinkUrl(dto.getLinkUrl());
				banner.setImageUrl(url);
				banner.setPosition(dto.getPosition());
				banner.setCreatedAt(LocalDateTime.now());
				banner.setIsActive(dto.getIsActive());
				BannerResponseDTO item = service.addBanner(banner);
				if(item!=null) {
					res.setMessage("created");
					res.setSuccess(true);
					return ResponseEntity.ok(res);
				}
				return null;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
}
