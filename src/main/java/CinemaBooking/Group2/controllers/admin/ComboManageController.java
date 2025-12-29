package CinemaBooking.Group2.controllers.admin;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.service.ComboService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@ResponseBody
public class ComboManageController {
	@Autowired
	ComboService service;
	@PutMapping("/api/combo/{id}")
	@CrossOrigin
	public String edit(@RequestBody Combo entity,@PathVariable("id")int id) {
		String m = "Error";
		try {
		 m =service.EditCombo(entity);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return m;
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
}
