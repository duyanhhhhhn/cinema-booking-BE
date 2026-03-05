package CinemaBooking.Group2.controllers.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.concession.ComboCRUDResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboListResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboRequestDTO;
import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.ComboItem;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.service.ComboService;
import CinemaBooking.Group2.service.ProductService;
import CinemaBooking.Group2.ultis.FileUltility;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@ResponseBody
public class ComboManageController {
	@Autowired
	ComboService service;
	@Autowired
	ProductService pro_service;
	@GetMapping("/public/combo")
	@CrossOrigin
	public ResponseEntity<ComboListResponseDTO> getCombo() {
		ComboListResponseDTO list = new ComboListResponseDTO();
		// String message = "Error";
		try {
			List<ComboResponseDTO> item = new ArrayList<>();
			item = service.getCombo();
			if (item == null) {
				list.setMessage("Not found any Combo");
				list.setSuccess(false);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(list);
			} else {
				list.setMessage("Success");
				list.setSuccess(true);
				return ResponseEntity.ok(list);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(list);
	}
	@PutMapping("/api/combo/{id}")
	@CrossOrigin
	public ResponseEntity<String> edit(@RequestBody Combo entity,@PathVariable("id")int id) {
		String m = "Error";
		Integer uid = getCurrentUserId();
		if(service.checkAdmin(uid)==0) {
			m = "You are not authorize to edit combo";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(m);
		}
		try {
		 m =service.EditCombo(entity);
		 return ResponseEntity.status(HttpStatus.CREATED).body(m);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(m);
	}
	@CrossOrigin
	@PostMapping("/api/public/combo/add")
	public ResponseEntity<ComboCRUDResponseDTO> add(@RequestBody ComboRequestDTO combo) {
		ComboCRUDResponseDTO res = new ComboCRUDResponseDTO();
		try {
			Combo item = new Combo();
			item.setName(combo.getName());
			item.setDescription("");
			item.setPrice(combo.getPrice());
			String imageName = FileUltility.uploadFileImage(combo.getBannerFile(), "uploads/concessions/combo","concessions/combo");
			item.setImageUrl(imageName);
			item.setCreatedAt(LocalDateTime.now());
			item.setIsActive(1);
			res = service.AddCombo(item);
			ObjectMapper mapper = new ObjectMapper();
		    List<ComboItem> comboItems =
		            mapper.readValue(
		                    combo.getItem(),
		                    new TypeReference<List<ComboItem>>() {}
		            );
			if(combo.getItem()!=null) {
				for(ComboItem items :comboItems) {
					items.setComboId(res.getCombo().getId());
					service.AddComboItem(items);
				}
			}
			return ResponseEntity.status(HttpStatus.CREATED).body(res);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
	}
	@PostMapping("/api/product/add")
	@CrossOrigin
	public ResponseEntity<ApiResponse<Product>> addProduct(@RequestParam("name")String name
			,@RequestParam("description") String description,@RequestParam("price")BigDecimal price,
			@RequestParam("bannerFile")MultipartFile image,@RequestParam("stock")int stock) {
		//TODO: process POST request
		try {
			Product item = new Product();
			item.setName(name);
			item.setDescription(description);
			item.setPrice(price);
			String imageName = FileUltility.uploadFileImage(image, "uploads/concessions/product","concessions/product");
			item.setImageUrl(imageName);
			item.setStock(stock);
			item.setCreatedAt(LocalDateTime.now());
			item.setIsActive(1);
			String a=pro_service.createProduct(item);
			return ResponseEntity.ok(new ApiResponse<Product>(a, item));
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return ResponseEntity.ok(null);
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
}
