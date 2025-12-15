package CinemaBooking.Group2.mappers;

import CinemaBooking.Group2.dtos.marketing.PostResponseDTO;
import CinemaBooking.Group2.models.Post;

public class PostMapper {
	public static PostResponseDTO toResponseDTO(Post post) {
		if(post==null) {
			return null;
		}
		PostResponseDTO item = new PostResponseDTO();
		try {
			item.setId(post.getId());
			item.setTitle(post.getTitle());
			item.setCategory(post.getCategory());
			item.setContent(post.getContent());
			item.setCoverUrl(post.getCoverUrl());
			item.setExcerpt(post.getExcerpt());
			item.setSlug(post.getSlug());
			item.setPublished(post.getPublished());
			item.setPublishedAt(post.getPublishedAt());
			item.setCreatedAt(post.getCreatedAt());
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return item;
	}
}
