package CinemaBooking.Group2.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.ProductMapper;
import CinemaBooking.Group2.models.Product;
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
	public Product findById(int id){
		Product item = new Product();
		try {
			item = db.query("select * from "+StringValue.tbl_product+" where id=?", new ProductMapper()
					,new Object[] {id}).get(0);
			return item;
		}
		catch (Exception e) {
			System.out.print(e);
			// TODO: handle exception
		}
		return item;
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
							product.getStock(),product.getIsActive(),product.getCreatedAt()});
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
		return 0;
	}
	@Override
	public int delete(int id) {
		// TODO Auto-generated method stub
		return 0;
	}
}
