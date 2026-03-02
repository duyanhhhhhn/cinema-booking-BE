package CinemaBooking.Group2.controllers.client;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.PageResponse;
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
	public ResponseEntity<ApiResponse<Banner>> getBannerById(@PathVariable("id") int id){
		ApiResponse<Banner> res;
		try {
			if(id==0) {
				res = new ApiResponse<Banner>("Banner id shouldn't be 0", null);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(res);
			}
			Banner item = service.getBannerById(id);
			if(item!=null) {
				res = new ApiResponse<Banner>("Success", item);
				return ResponseEntity.ok(res);
			}
			else if(item==null) {
				//banner.setMessage("Not Found");
				//banner.setStatus(false);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<Banner>("error", null));
	}
	@GetMapping("/banner/get")
	public ResponseEntity<ApiResponse<List<BannerResponseDTO>>> getBannerByPosition(@RequestParam("position")BannerPosition position,@RequestParam("count")int count){
		 List<BannerResponseDTO> list = new ArrayList<>();
		try {
			if(position==null) {
				return ResponseEntity.ok(new ApiResponse<List<BannerResponseDTO>>("position shouldn't empty", null));
			}
			else {
				list = service.getBannerByPosition(position, count);
				if(list==null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<List<BannerResponseDTO>>("not found ", list));
				}
				return ResponseEntity.ok(new ApiResponse<List<BannerResponseDTO>>("success", list));
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<List<BannerResponseDTO>>("error",null));
	}
	@PostMapping("/admin/banner")
	//@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<PageResponse<BannerResponseDTO>> addBanner(@RequestBody() BannerRequestDTO dto,@RequestParam("bannerFile")MultipartFile bannerFile){
		PageResponse<BannerResponseDTO> res = new PageResponse<>();
		try {
			if(dto==null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
			}
			else {
				String url =FileUltility.uploadFileImage(bannerFile, "uploads/banner","banner");
				Banner banner = new Banner();
				banner.setTitle(dto.getTitle());
				banner.setLinkUrl(dto.getLinkUrl());
				banner.setImageUrl(url);
				banner.setPosition(dto.getPosition());
				banner.setCreatedAt(LocalDateTime.now());
				banner.setIsActive(true);
				BannerResponseDTO item = service.addBanner(banner);
				if(item!=null) {
					res.setMessage("created");
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
	@PutMapping("/banner/{id}")
	//@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<PageResponse<BannerResponseDTO>> updateBanner(
			@RequestBody() BannerRequestDTO dto,
			@PathVariable(name = "id")int id,@RequestParam(name="bannerFile",required = false)MultipartFile bannerFile){
		PageResponse<BannerResponseDTO> res = new PageResponse<>();
		try {
			if(dto==null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
			}
			Banner banner = new Banner();
			if(bannerFile!=null) {
				String url =FileUltility.uploadFileImage(bannerFile, "uploads/banner","banner");
				banner.setImageUrl(url);
			}
			else {
				banner.setImageUrl(dto.getImageUrl());
			}
				banner.setTitle(dto.getTitle());
				banner.setLinkUrl(dto.getLinkUrl());
				banner.setPosition(dto.getPosition());
				banner.setIsActive(true);
				banner.setId(id);
				BannerResponseDTO item = service.updateBanner(banner);
				if(item!=null) {
					res.setMessage("updated");
					return ResponseEntity.ok(res);
				}
				return null;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	@DeleteMapping("/admin/banner/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<PageResponse<BannerResponseDTO>> deleteBanner(@PathVariable(name="id",required = false)int id){
		PageResponse<BannerResponseDTO> res = new PageResponse<>();
		try {
			if(id==0) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
			}
			Banner banner = service.getBannerById(id);
			if(banner!=null) {
				BannerResponseDTO item=service.deleteBanner(id);
				FileUltility.deleteFile("uploads/banner", banner.getImageUrl());
				if(item!=null) {
					res.setMessage("deleted");
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
