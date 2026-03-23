package CinemaBooking.Group2.repositories.concessions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.ProductMapper;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.repositories.Icrud;
import CinemaBooking.Group2.ultis.StringValue;

@Repository
public class ProductRepository implements Icrud<Product>{
	@Autowired
	private JdbcTemplate db;
	public ProductRepository() {
		
	}
	public List<Product> getAll(){
		List<Product> item = new ArrayList<>();
		try {
			item = db.query("select * from "+StringValue.tbl_product, new ProductMapper());
			return item;
		}
		catch (Exception e) {
			System.out.print(e);
			// TODO: handle exception
		}
		return item;
	}
	public List<Product> getAll(int page,int size){
		List<Product> item = new ArrayList<>();
		try {
			int value = (page-1)*size;
			item = db.query("select * from "+StringValue.tbl_product
					+" limit ? offset ?", new ProductMapper(),new Object[] {
							size,value});
			return item;
		}
		catch (Exception e) {
			System.out.print(e);
			// TODO: handle exception
		}
		return item;
	}
	public Product findById(int id){
		try {
			List<Product> results = db.query("select * from "+StringValue.tbl_product+" where id=?", new ProductMapper()
					,new Object[] {id});
			if (results != null && !results.isEmpty()) {
				return results.get(0);
			}
		}
		catch (Exception e) {
			System.out.print(e);
		}
		return null;
	}
	public List<Product> Paging(int page,int size){
		List<Product> item = new ArrayList<>();
		try {
			int value = (page-1)*size;
			item = db.query("select * from "+StringValue.tbl_product+" limit ? offset ?", 
					new ProductMapper(),new Object[] {size,value});
			return item;
		}
		catch (Exception e) {
			System.out.print(e);
			// TODO: handle exception
		}
		return item;
	}
	@Override
	public int create(Product product) {
		try {
			int rs = db.update("insert into "+StringValue.tbl_product+"(name,description,price,image_url,stock,is_active,created_at) value(?,?,?,?,?,?,?)",
					new Object[] {product.getName(),product.getDescription(),product.getPrice(),product.getImageUrl(),
							product.getStock(),1,product.getCreatedAt()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return 0;
	}
	@Override
	public List<Product> search(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int update(Product item) {
		// TODO Auto-generated method stub
		try {
			int rs=0;
			if(item.getImageUrl()!=null) {
				rs= db.update("update "+StringValue.tbl_product+" set name=?,description=?,price=?,image_url=?,stock=? where id=?",
						new Object[] {item.getName(),item.getDescription(),item.getPrice(),item.getImageUrl(),
								item.getStock(),item.getId()});
			}
			else {
				rs= db.update("update "+StringValue.tbl_product+" set name=?,description=?,price=?,stock=? where id=?",
						new Object[] {item.getName(),item.getDescription(),item.getPrice(),
								item.getStock(),item.getId()});
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
		// TODO Auto-generated method stub
		try {
			int rs = 0;
			rs=db.update("delete from "+StringValue.tbl_product+" where id=?",new Object[] {id});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}
}