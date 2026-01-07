package CinemaBooking.Group2.controllers.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
