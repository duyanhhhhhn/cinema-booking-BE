package CinemaBooking.Group2.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.ComboItemMapper;
import CinemaBooking.Group2.mappers.ComboMapper;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.ComboItem;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class ComboRepository implements Icrud<Combo>{
	@Autowired
	private JdbcTemplate db;
	public ComboRepository() {
		
	}
	public List<Combo> getAll(){
		List<Combo> list = new ArrayList<>();
		try {
			String sql = "select * from " + StringValue.tbl_combo + " where is_active=1";
			System.out.println("=== DEBUG ComboRepository.getAll SQL: " + sql);
			list = db.query(sql, new ComboMapper());
			System.out.println("=== DEBUG ComboRepository.getAll returned " + list.size() + " combos");
		}
		catch(Exception e) {
			System.err.println("=== ERROR in ComboRepository.getAll: " + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	public List<Combo> Paging(int page,int size){
		List<Combo> list = new ArrayList<>();
		try {
			int value=(page-1)*size;
			String sql = "select * from "+StringValue.tbl_combo+" where is_active=1 limit ? offset ?";
			System.out.println("=== DEBUG ComboRepository.Paging SQL: " + sql);
			System.out.println("=== DEBUG Parameters: size=" + size + ", offset=" + value);
			list = db.query(sql, new ComboMapper(), new Object[] {size,value});
			System.out.println("=== DEBUG ComboRepository.Paging returned " + list.size() + " combos");
			return list;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.err.println("=== ERROR in ComboRepository.Paging: " + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	public Combo findById(int id){
		try {
			List<Combo> results = db.query("select * from "+StringValue.tbl_combo+
					" where id=? and is_active=1", new ComboMapper(),new Object[] {id});
			if (results != null && !results.isEmpty()) {
				return results.get(0);
			}
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return null;
	}
	@Override
	public int create(Combo item) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			db.update(connection -> {
			    PreparedStatement ps = connection.prepareStatement(
			        "INSERT INTO " + StringValue.tbl_combo +
			        " (name, description, price, image_url, is_active, created_at) VALUES (?,?,?,?,?,?)",
			        Statement.RETURN_GENERATED_KEYS
			    );
			    ps.setString(1, item.getName());
			    ps.setString(2, item.getDescription());
			    ps.setBigDecimal(3, item.getPrice());
			    ps.setString(4, item.getImageUrl());
			    ps.setInt(5, item.getIsActive());
			    ps.setTimestamp(6, Timestamp.valueOf(item.getCreatedAt()));
			    return ps;
			}, keyHolder);
			int comboId = keyHolder.getKey().intValue();
			return comboId;
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return 0;
	}
	@Override
	public int update(Combo item) {
		try {
			int rs =0;
			if(item.getImageUrl()==null) {
				rs = db.update("update " + StringValue.tbl_combo +
						" set name=?,description=?,price=?,is_active=?,created_at=? where id=?",
						new Object[] {item.getName(),item.getDescription(),item.getPrice(),
								item.getIsActive(),item.getCreatedAt(),item.getId()});
			}
			else {
				rs = db.update("update " + StringValue.tbl_combo +
						" set name=?,description=?,price=?,image_url=?,is_active=?,created_at=? where id=?",
						new Object[] {item.getName(),item.getDescription(),item.getPrice(),
								item.getImageUrl(),item.getIsActive(),item.getCreatedAt(),item.getId()});
			}
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
			int rs=db.update("delete from " + StringValue.tbl_combo + " where id=?",
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
			int rs = db.update("update " + StringValue.tbl_combo +
					" set is_active=? where id=?",new Object[] {active,id});
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
	public int compareComboItem(int id,ComboItem item) {
		try {
			List<ComboItem> items = db.query("select * from "+StringValue.tbl_comboItem+" where combo_id=?", new ComboItemMapper(),new Object[] {id});
			for(ComboItem item2 : items) {
				if(item.getId()==item2.getId()) {
					if(item.getQuantity()==item2.getQuantity()) {
						return 2;
					}
					return 1;
				}
				else if(item.getId()==0){
					return 0;
				}
			}
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
}