package CinemaBooking.Group2.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.PostMapper;
import CinemaBooking.Group2.models.Post;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class PostRepository implements Icrud<Post>{
	@Autowired
	private JdbcTemplate db;
	public PostRepository() {
		
	}
	public List<Post> getAll(){
		List<Post> item = new ArrayList<>();
		try {
			item = db.query("select * from "+StringValue.tbl_post+" where is_published=1",new PostMapper());
		}
		catch(Exception e) {
			throw new RuntimeException();
		}
		return item;
	}
	public List<Post> Paging(int page,int size){
		List<Post> item = new ArrayList<>();
		try {
			int value = (page-1)*size;
			item = db.query("select * from "+StringValue.tbl_post+
					" limit ? offset ?",new PostMapper()
					,new Object[] {size,value});
			return item;
		}
		catch(Exception e) {
			System.out.print(e.getMessage());
		}
		return item;
	}
	public int newPost(Post post) {
		try {
			int rs = db.update("insert into "+StringValue.tbl_post+"(title,slug,excerpt,content,cover_url,is_published) values(?,?,?,?,?,?)"
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
			item = db.query("select * from "+StringValue.tbl_post+" where id=?", 
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
		List<Post> item = new ArrayList<>();
		try {
			item = db.query("select * from"+StringValue.tbl_post+"where title like %?% or content ", 
					new PostMapper(),new Object[] {key});
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
	public List<Post> search(String key,int page,int size) {
		// TODO Auto-generated method stub
		List<Post> item = new ArrayList<>();
		try {
			int value = (page-1)*size;
			item = db.query("select * from"+StringValue.tbl_post+"limit ? offset ? where title like %?% or content ", 
					new PostMapper(),new Object[] {size,value,key});
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
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
			int rs = db.update("update "+StringValue.tbl_post+" set title=?,slug=?,excerpt=?,content=?,cover_url=?,is_published=? where id=?",
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
			int rs = db.update("delete from "+StringValue.tbl_post+" where id=?",new Object[] {id});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		
		System.out.print(e.getMessage());
		}
		return 0;
	}
}
