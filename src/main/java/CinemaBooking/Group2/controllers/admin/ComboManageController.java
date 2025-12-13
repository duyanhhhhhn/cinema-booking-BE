package CinemaBooking.Group2.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
}
