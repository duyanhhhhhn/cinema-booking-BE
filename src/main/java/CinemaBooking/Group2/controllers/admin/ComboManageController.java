package CinemaBooking.Group2.controllers.admin;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.service.ComboService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@ResponseBody
public class ComboManageController {
	@Autowired
	ComboService service;
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
		 return ResponseEntity.ok(m);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(m);
	}
	@CrossOrigin
	@PostMapping("/api/combo/add")
	public String add(@RequestParam("name") String name,@RequestParam("descriptiom") String description,
			@RequestParam("price")BigDecimal price,@RequestParam("image_url")String image) {
		String m ="Error";
		try {
			Combo item = new Combo();
			item.setName(name);
			item.setDescription(description);
			item.setPrice(price);
			item.setImageUrl(image);
			return service.AddCombo(item);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return m;
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
        }
        return null;
    }
}
