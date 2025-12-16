package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.marketing.PostResponseDTO;
import CinemaBooking.Group2.mappers.PostMapper;
import CinemaBooking.Group2.models.Post;
import CinemaBooking.Group2.repositories.ModelMaker;

@Service
public class PostService {
	public List<PostResponseDTO> getAllPost() {
		try {
			List<Post> item = ModelMaker.Instance().getPost();
			return item.stream().map(PostMapper::toResponseDTO).collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
	public String newPost(Post post) {
		String s = "Failed to Create New Post";
		try {
			int rs=ModelMaker.Instance().createPost(post);
			if(rs==1) {
				s="Success";
			}
			return s;
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
}
