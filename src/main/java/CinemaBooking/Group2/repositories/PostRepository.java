package CinemaBooking.Group2.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.models.DbConnection;
import CinemaBooking.Group2.models.Post;

public class PostRepository implements Icrud<Post>{
	private JdbcTemplate db;
	private static PostRepository _instance=null;
	public static PostRepository Instance() {
		if(_instance==null) {
			_instance=new PostRepository();
		}
		return _instance;
	}
	private PostRepository() {
		db=DbConnection.Instance().getDb();
	};
	class PostMapper implements RowMapper<Post>{

		@Override
		public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			Post post = new Post();
			post.setId(rs.getInt("id"));
			post.setTitle(rs.getString("title"));
			post.setSlug(rs.getString("slug"));
			post.setExcerpt(rs.getString("excerpt"));
			post.setContent(rs.getString("content"));
			post.setCoverUrl(rs.getString("cover_url"));
			post.setPublished(rs.getInt("is_published"));
			//post.setPublishedAt(rs.getDate("published_at"));
			//post.setCreatedAt();
			return post;
		}		
	}
	public List<Post> getAll(){
		List<Post> item = new ArrayList<>();
		try {
			item = db.query("select * from `posts` where is_published=1",new PostMapper());
		}
		catch(Exception e) {
			throw new RuntimeException();
		}
		return item;
	}
	public int newPost(Post post) {
		try {
			int rs = db.update("insert into `post`(title,slug,excerpt,content,cover_url,is_published) values(?,?,?,?,?,?)"
					,new Object[] {post.getTitle(),post.getSlug(),post.getExcerpt(),post.getContent(),
							post.getCoverUrl(),post.getPublished()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return 0;
	}
	@Override
	public Post findById(int id) {
		// TODO Auto-generated method stub
		Post item = new Post();
		try {
			item = db.query("select * from `post` where id=?", 
					new PostMapper(),new Object[] {id}).get(0);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	@Override
	public List<Post> search(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int create(Post item) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int update(Post item) {
		// TODO Auto-generated method stub
		try {
			int rs = db.update("update `post` set title=?,slug=?,excerpt=?,content=?,cover_url=?,is_published=? where id=?",
					new Object[] {item.getTitle(),item.getSlug(),item.getExcerpt(),
							item.getContent(),item.getCoverUrl(),item.getPublished()}, item.getId());
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}
	@Override
	public int delete(int id) {
		// TODO Auto-generated method stub
		try {
			int rs = db.update("delete from `post` where id=?",new Object[] {id});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		
		System.out.print(e.getMessage());
		}
		return 0;
	}
}
