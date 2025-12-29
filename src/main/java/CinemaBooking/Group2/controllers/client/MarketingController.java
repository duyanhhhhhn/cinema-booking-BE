package CinemaBooking.Group2.controllers.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.marketing.PostResponseDTO;
import CinemaBooking.Group2.models.Post;
import CinemaBooking.Group2.service.MarketingService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class MarketingController {
	@Autowired
	MarketingService service;
	@GetMapping("/api/posts")
	@CrossOrigin
	public ResponseEntity<List<PostResponseDTO>> getPost(){
		List<PostResponseDTO> item = new ArrayList<>();
		return ResponseEntity.ok(item); 
	}
	@PostMapping("/api/posts")
	@CrossOrigin
	public ResponseEntity<String> newPost(@RequestBody Post item){
		String rs = service.newPost(item);
		return ResponseEntity.ok(rs);
	}
}
