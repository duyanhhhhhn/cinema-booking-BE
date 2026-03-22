package CinemaBooking.Group2.controllers.admin;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.home.BannerRequestDTO;
import CinemaBooking.Group2.dtos.home.BannerResponseDTO;
import CinemaBooking.Group2.models.Banner;
import CinemaBooking.Group2.service.BannerService;
import CinemaBooking.Group2.ultis.FileUltility;

@RequestMapping("/api")
@RestController
public class HomeAdminController {
	@Autowired
	private BannerService service;	
	@PostMapping("/admin/banner")
	//@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<PageResponse<BannerResponseDTO>> addBanner(@ModelAttribute BannerRequestDTO dto){
		PageResponse<BannerResponseDTO> res = new PageResponse<>();
		try {
			if(dto==null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
			}
			else {
				String url =FileUltility.uploadFileImage(dto.getBannerFile(), "uploads/banner","banner");
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
	@PutMapping("/admin/banner/{id}")
	//@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<PageResponse<BannerResponseDTO>> updateBanner(
			@ModelAttribute BannerRequestDTO dto,
			@PathVariable(name = "id")int id){
		PageResponse<BannerResponseDTO> res = new PageResponse<>();
		try {
			if(dto==null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
			}
			Banner banner = new Banner();
			if(dto.getBannerFile()!=null) {
				String url =FileUltility.uploadFileImage(dto.getBannerFile(), "uploads/banner","banner");
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
