package CinemaBooking.Group2.repositories;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import CinemaBooking.Group2.mappers.BannerMapper;
import CinemaBooking.Group2.models.Banner;
import CinemaBooking.Group2.models.DbConnection;

public class BannerRepository implements Icrud<Banner>{
	private static BannerRepository _instance;
	private JdbcTemplate db;
	private BannerRepository() {
		db = DbConnection.Instance().getDb();
	}
	public static BannerRepository Instance() {
		if(_instance==null) {
			_instance = new BannerRepository();
		}
		return _instance;
	}
	@Override
	public List<Banner> getAll() {
		// TODO Auto-generated method stub
		List<Banner> item=null;
		try {
			item = db.query("select * from `banner`", new BannerMapper());
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
			item = db.query("select * from `banner` where id=?", new BannerMapper(),new Object[] {id}).get(0);
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
			int rs = db.update("insert into `banner`(title,image_url,link_url,position,is_active,created_at) values(?,?,?,?,?,?)", 
					new Object[] {item.getTitle(),item.getImageUrl(),item.getLinkUrl(),item.getPosition(),
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
			int rs = db.update("update`banner` set title=?,image_url=?,link_url=?,position=?,is_active=?,created_at=?) where id=?", 
					new Object[] {item.getTitle(),item.getImageUrl(),item.getLinkUrl(),item.getPosition(),
							item.getIsActive(),item.getCreatedAt(),item.getId()});
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
			int rs = db.update("delete from `banner` where id=?",new Object[] {id});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public List<Banner> findByLinkURL(String link_url){
		try {
			List<Banner> item = db.query("select * from `banner` where link_url=?",
					new BannerMapper(), new Object[] {link_url});
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
}
