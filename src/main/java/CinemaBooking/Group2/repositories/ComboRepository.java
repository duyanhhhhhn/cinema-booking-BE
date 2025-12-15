package CinemaBooking.Group2.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.Combo;

@Repository
public class ComboRepository {
	@Autowired
	private JdbcTemplate db;
	@Autowired
	private ComboItemRepository rep;
    public ComboRepository() {
		
	}
	public class ComboRowMapper implements RowMapper<Combo>{

		@Override
		public Combo mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			Combo item = new Combo();
			item.setId(rs.getInt("id"));
			item.setName(rs.getNString("name"));
			item.setDescription(rs.getNString("description"));
			item.setPrice(rs.getBigDecimal("price"));
			item.setImageUrl(rs.getNString("image_url"));
			item.setIsActive(rs.getInt("is_active"));
			item.setComboItems(rep.getByCombo(item.getId()));
			return item;
		}
		
	}
	public List<Combo> getAll(){
		List<Combo> list = new ArrayList<>();
		try {
			list = db.query("select * from `combos`", new ComboRowMapper());
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
			list = db.query("select * from `combos` limit ? offset ?", new ComboRowMapper(),new Object[] {size,value});
			return list;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return list;
	}
	public List<Combo> findById(int id){
		List<Combo> list = new ArrayList<>();
		try {
			list = db.query("select * from `combos` where id=?", new ComboRowMapper(),new Object[] {id});
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return list;
	}
	public int Create(Combo item) {
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
	public int Update(Combo item) {
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
	public int Delete(int id) {
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
}
