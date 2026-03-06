package CinemaBooking.Group2.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.ComboItemMapper;
import CinemaBooking.Group2.models.ComboItem;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class ComboItemRepository implements Icrud<ComboItem>{
	@Autowired
	private JdbcTemplate db;
	public ComboItemRepository() {
		// TODO Auto-generated constructor stub
	}
	public List<ComboItem> getAll() {
		List<ComboItem> list = new ArrayList<>();
		try {
			list = db.query("select * from "+StringValue.tbl_comboItem, new ComboItemMapper());
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return list;
	}
	public ComboItem findById(int id) {
		ComboItem item = new ComboItem();
		try {
			item = db.query("select * from "+StringValue.tbl_comboItem +" where id=?", 
					new ComboItemMapper(),new Object[] {id}).get(0);
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return item;
	}
	public List<ComboItem> getByCombo(int order_id){
		List<ComboItem> item = new ArrayList<>();
		try {
			item = db.query("select * from "+StringValue.tbl_comboItem +" where combo_id=?", 
					new ComboItemMapper(),new Object[] {order_id});
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return item;
	}
	public int deleteByCombo(int idCombo) {
		try {
			int rs = db.update("delete from "+StringValue.tbl_comboItem+" where combo_id=?", new Object[] {idCombo});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public int create(ComboItem item) {
		try {
			if(check(item)) {
				int rs=db.update("update "+StringValue.tbl_comboItem+" set quantity=? where id=?",new Object[] {item.getQuantity(),item.getId()});
				return rs;
			}
			int rs = db.update("insert into "+StringValue.tbl_comboItem +"(combo_id,product_id,quantity) values(?,?,?)",
					new Object[] {item.getComboId(),item.getProductId(),item.getQuantity()});
			return rs;
		}
		catch(Exception e) {
			System.out.print(e);
		}
		return 0;
	}
	public boolean check(ComboItem item) {
		try {
			ComboItem cbi = db.query("select * from "+StringValue.tbl_comboItem+" where product_id=? and combo_id=?",new ComboItemMapper(),new Object[] {item.getProductId(),item.getComboId()}).get(0);
			if(cbi!=null) {
					return true;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return false;
	}
	public int update(ComboItem item) {
		try {
			int rs = db.update("update "+StringValue.tbl_comboItem +" set combo_id=?,product_id=?,quantity=? where id=?",
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
			int rs = db.update("update "+StringValue.tbl_comboItem +" set quantity=? where id=?",
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
			int rs = db.update("delete from "+StringValue.tbl_comboItem +" where id=?",
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
			item = db.query("select * from "+StringValue.tbl_comboItem +" where id=?", new ComboItemMapper(),new Object[] {id}).get(0);
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
