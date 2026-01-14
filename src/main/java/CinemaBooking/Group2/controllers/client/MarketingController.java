package CinemaBooking.Group2.controllers.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.concession.VoucherResponseDTO;
import CinemaBooking.Group2.dtos.marketing.ListPostResponseDTO;
import CinemaBooking.Group2.dtos.marketing.PostResponseDTO;
import CinemaBooking.Group2.models.Post;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.service.MarketingService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class MarketingController {
	@Autowired
	MarketingService service;
	@GetMapping("/api/posts")
	@CrossOrigin
	public ResponseEntity<ListPostResponseDTO> getPost(){
		ListPostResponseDTO item = service.getAllPost();
		try {
			
			if(item.isIs_success()==false) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(item);
			}
			else {
				return ResponseEntity.ok(item);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(item); 
	}
	@GetMapping("/api/posts/paging")
	@CrossOrigin
	public ResponseEntity<ListPostResponseDTO> getPost(@RequestParam("page")int page,
			@RequestParam("size")int size){
		ListPostResponseDTO item = service.getPostPaging(page,size);
		try {
			
			if(item.isIs_success()==false) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(item);
			}
			else {
				return ResponseEntity.ok(item);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(item); 
	}
	@PostMapping("/api/posts")
	@CrossOrigin
	public ResponseEntity<PostResponseDTO> newPost(@RequestBody Post item){
		PostResponseDTO rs = new PostResponseDTO();
		try {
			if(getCurrentUserId()==null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(rs);
			}
			rs=service.newPost(item);
			if(rs.getIsSuccess()==false) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rs);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(rs);
	}
	@GetMapping("/api/posts/{id}")
	@CrossOrigin
	public ResponseEntity<PostResponseDTO> postInfo(@PathVariable("id")int id){
		PostResponseDTO item = new PostResponseDTO();
		try {
			item = service.postInfo(id);
			if(item==null) {
				item =  new PostResponseDTO();
				item.setMessage("Not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(item);
			}
			return ResponseEntity.ok(item);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		item.setMessage("Error");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(item);
	}
	@GetMapping("/api/vouchers/paging")
	@CrossOrigin
	public ResponseEntity<List<VoucherResponseDTO>> getVoucherPaging(@RequestParam("page")int page,
			@RequestParam("size") int size){
		List<VoucherResponseDTO> item=null;
		try {
			item= service.getVoucherPaging(page,size);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.ok(item);
		}
	private Integer getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof User) {
                    return ((User) principal).getId();
                }
            }
        } catch (Exception e) {
            // Return null if any error occurs
        	System.out.print(e.getMessage());
        }
        return null;
    }
	public ResponseEntity<VoucherResponseDTO> checkVoucher(@RequestParam("id") int id,@RequestParam("price")BigDecimal price) {
		VoucherResponseDTO rs = new VoucherResponseDTO();
		try {
			 rs = service.checkDiscount(id, price);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.ok(rs);
	}
}
