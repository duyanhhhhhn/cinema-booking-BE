package CinemaBooking.Group2.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.BannerMapper;
import CinemaBooking.Group2.models.Banner;
import CinemaBooking.Group2.models.Banner.BannerPosition;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class BannerRepository implements Icrud<Banner>{
	@Autowired
	private JdbcTemplate db;
	public BannerRepository() {
	}
	@Override
	public List<Banner> getAll() {
		// TODO Auto-generated method stub
		List<Banner> item=null;
		try {
			item = db.query("select * from "+StringValue.tbl_banner, new BannerMapper());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return item;
	}

	@Override
	public Banner findById(int id) {
		// TODO Auto-generated method stub
		Banner item = null;
		try {
			item = db.query("select * from "+StringValue.tbl_banner+" where id=?", new BannerMapper(),new Object[] {id}).get(0);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return item;
	}

	@Override
	public List<Banner> search(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int create(Banner item) {
		// TODO Auto-generated method stub
		try {
			int rs = db.update("insert into "+StringValue.tbl_banner+"(title,image_url,link_url,position,is_active,created_at) values(?,?,?,?,?,?)", 
					new Object[] {item.getTitle(),item.getImageUrl(),item.getLinkUrl(),item.getPosition().name(),
							item.getIsActive(),item.getCreatedAt()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}

	@Override
	public int update(Banner item) {
		try {
			int rs = db.update("update "+StringValue.tbl_banner+" set title=?,image_url=?,link_url=?,position=?,is_active=? where id=?", 
					new Object[] {item.getTitle(),item.getImageUrl(),item.getLinkUrl(),item.getPosition().name(),
							item.getIsActive(),item.getId()});
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
			if(findById(id)==null) {
				return 0;
			};
			int rs = db.update("delete from "+StringValue.tbl_banner+" where id=?",new Object[] {id});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public List<Banner> findByLinkURL(String link_url){
		try {
			List<Banner> item = db.query("select * from "+StringValue.tbl_banner+" where link_url=?",
					new BannerMapper(), new Object[] {link_url});
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public List<Banner> findByLinkURL(String link_url,int count){
		try {
			List<Banner> item = db.query("select * from "+StringValue.tbl_banner+"limit ? where link_url=?",
					new BannerMapper(), new Object[] {count,link_url});
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public List<Banner> findByPosition(BannerPosition position,int count){
		try {
			List<Banner> item = db.query("select * from "+StringValue.tbl_banner+" where position=? limit ? ",
					new BannerMapper(), new Object[] {position.name(),count});
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return null;
	}
}
