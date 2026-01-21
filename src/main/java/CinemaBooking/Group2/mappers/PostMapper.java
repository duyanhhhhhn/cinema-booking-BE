package CinemaBooking.Group2.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.dtos.marketing.PostResponseDTO;
import CinemaBooking.Group2.models.Post;

public class PostMapper implements RowMapper<Post>{
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
	@Override
	public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Post post = new Post();
		post.setId(rs.getInt("id"));
		if(rs.getString("title")!=null) {
			post.setTitle(rs.getString("title"));
		}
		if(rs.getString("slug")!=null) {
			post.setSlug(rs.getString("slug"));
		}
		if(rs.getString("excerpt")!=null) {
			post.setExcerpt(rs.getString("excerpt"));
		}
		if(rs.getString("content")!=null) {
			post.setContent(rs.getString("content"));
		}
		if(rs.getString("cover_url")!=null) {
			post.setCoverUrl(rs.getString("cover_url"));
		}
		if(rs.getInt("is_published")!=0) {
			post.setPublished(rs.getInt("is_published"));
		}
		if(rs.getString("category")!=null) {
			post.setCategory(rs.getString("category"));
		}
		if(rs.getTimestamp("published_at").toLocalDateTime()!=null) {
			post.setPublishedAt(rs.getTimestamp("published_at").toLocalDateTime());
		}
		//post.setCreatedAt();
		return post;
	}		
}
