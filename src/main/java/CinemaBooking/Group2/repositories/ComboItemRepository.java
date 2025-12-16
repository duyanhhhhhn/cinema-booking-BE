package CinemaBooking.Group2.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import CinemaBooking.Group2.models.ComboItem;
import CinemaBooking.Group2.models.DbConnection;

public class ComboItemRepository implements Icrud<ComboItem>{
	private JdbcTemplate db = DbConnection.Instance().getDb();
	private static ComboItemRepository _instance=null;
	private ComboItemRepository() {
	}
	public static ComboItemRepository Instance() {
		if(_instance==null) {
			_instance= new ComboItemRepository();
		}
		return _instance;
	}
	public class ComboItemRowMapper implements RowMapper<ComboItem> {
		@Override
		public ComboItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			ComboItem item = new ComboItem();
			item.setId(rs.getInt("id"));
			item.setComboId(rs.getInt("combo_id"));
			item.setProductId(rs.getInt("product_id"));
			item.setQuantity(rs.getInt("quantity"));
			return item;
		}
	}
	public List<ComboItem> getAll() {
		List<ComboItem> list = new ArrayList<>();
		try {
			list = db.query("select * from `combo_items`", new ComboItemRowMapper());
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return list;
	}
	public ComboItem findById(int id) {
		ComboItem item = new ComboItem();
		try {
			item = db.query("select * from `combo_items` where id=?", 
					new ComboItemRowMapper(),new Object[] {id}).get(0);
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return item;
	}
	public List<ComboItem> getByCombo(int order_id){
		List<ComboItem> item = new ArrayList<>();
		try {
			item = db.query("select * from `combo_items` where combo_id=?", 
					new ComboItemRowMapper(),new Object[] {order_id});
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return item;
	}
	public int create(ComboItem item) {
		try {
			int rs = db.update("insert into `combo_items`(combo_id,product_id,quantity) values(?,?,?)",
					new Object[] {item.getComboId(),item.getProductId(),item.getQuantity()});
			return rs;
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return 0;
	}
	public int update(ComboItem item) {
		try {
			int rs = db.update("update `combo_items` set combo_id=?,product_id=?,quantity=? where id=?",
					new Object[] {item.getComboId(),item.getProductId(),item.getQuantity(),item.getId()});
			return rs;
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return 0;
	}
	public int AddQuantity(ComboItem item,int quantity) {
		try {
			int total=quantity+=item.getQuantity();
			int rs = db.update("update `combo_items` set quantity=? where id=?",
					new Object[] {total,item.getId()});
			return rs;
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return 0;
	}
	public int delete(int id) {
		try {
			if (checkExist(id)) {
				return 0;
			}
			int rs = db.update("delete from `combo_items` where id=?",
					new Object[] {id});
			return rs;
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return 0;
	}
	@Override
	public List<ComboItem> search(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean checkExist(int id) {
		try {
			ComboItem item=null;
			item = db.query("select * from `combo_items` where id=?", new ComboItemRowMapper(),new Object[] {id}).get(0);
			if(item!=null) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return false;
	}
}
