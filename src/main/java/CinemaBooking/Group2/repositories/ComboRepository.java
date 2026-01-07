package CinemaBooking.Group2.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.ComboMapper;
import CinemaBooking.Group2.models.Combo;

@Repository
public class ComboRepository implements Icrud<Combo>{
	@Autowired
	private JdbcTemplate db;
	public ComboRepository() {
		
	}
	public List<Combo> getAll(){
		List<Combo> list = new ArrayList<>();
		try {
			list = db.query("select * from `combos` where is_active=1", new ComboMapper());
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return list;
	}
	public List<Combo> Paging(int page,int size){
		List<Combo> list = new ArrayList<>();
		try {
			int value=(page-1)*size;
			list = db.query("select * from `combos` limit ? offset ? where is_active=1", new ComboMapper(),new Object[] {size,value});
			return list;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return list;
	}
	public Combo findById(int id){
		Combo item = new Combo();
		try {
			item = db.query("select * from `combos` where id=? and is_active=1", new ComboMapper(),new Object[] {id}).get(0);
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return item;
	}
	@Override
	public int create(Combo item) {
		try {
			int rs = db.update("insert into `combo`(name,description,price,image_url,is_active,create_at) values(?,?,?,?,?,?)",
					new Object[] {item.getName(),item.getDescription(),item.getPrice(),
							item.getImageUrl(),item.getIsActive(),item.getCreatedAt()});
			return rs;
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return 0;
	}
	@Override
	public int update(Combo item) {
		try {
			int rs = db.update("update `combos` set name=?,description=?,price=?,image_url=?,is_active=?,created_at=? where id=?",
					new Object[] {item.getName(),item.getDescription(),item.getPrice(),
							item.getImageUrl(),item.getIsActive(),item.getCreatedAt(),item.getId()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return 0;
	}
	@Override
	public int delete(int id) {
		try {
			int rs=db.update("delete from `combos` where id=?",
					new Object[] {id});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return 0;
	}
	public int ChangeActive(int id,int active) {
		try {
			int rs = db.update("update `combos` set is_active=? where id=?",new Object[] {active,id});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return 0;
	}
	@Override
	public List<Combo> search(String key) {
		// TODO Auto-generated method stub
		return null;
	}
}
