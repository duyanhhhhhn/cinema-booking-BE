package CinemaBooking.Group2.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.Product;

@Repository
public class ProductRepository {
	@Autowired
	private JdbcTemplate db;
	public ProductRepository () {
		
	}
	public class ProductRowMapper implements RowMapper<Product>{

		@Override
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			Product pro = new Product();
			pro.setId(rs.getInt("id"));
			pro.setName(rs.getNString("name"));
			pro.setDescription(rs.getNString("description"));
			pro.setPrice(rs.getBigDecimal("price"));
			pro.setStock(rs.getInt("stock"));
			pro.setIsActive(rs.getInt("is_active"));
			pro.setImageUrl(rs.getNString("image_url"));
			return pro;
		}
		
	}
	public List<Product> getAll(){
		List<Product> item = new ArrayList<>();
		try {
			item = db.query("select * from `products`", new ProductRowMapper());
			return item;
		}
		catch (Exception e) {
			System.out.print(e);
			// TODO: handle exception
		}
		return item;
	}
	public List<Product> findById(int id){
		List<Product> item = new ArrayList<>();
		try {
			item = db.query("select * from `products` where id=?", new ProductRowMapper()
					,new Object[] {id});
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
			item = db.query("select * from `products` limit ? offset ?", 
					new ProductRowMapper(),new Object[] {size,value});
			return item;
		}
		catch (Exception e) {
			System.out.print(e);
			// TODO: handle exception
		}
		return item;
	}
	public int CreateProduct(Product product) {
		try {
			int rs = db.update("insert into `products`(id,name,description,price,image_url,stock,is_active,create_at) value(?,?,?,?,?,?,?,?)",
					new Object[] {product.getId(),product.getName(),product.getDescription(),product.getPrice(),product.getImageUrl(),
							product.getStock(),product.getIsActive(),product.getCreatedAt()});
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return 0;
	}
}
