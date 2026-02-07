package CinemaBooking.Group2.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.mappers.CinemaMapper;
import CinemaBooking.Group2.models.Cinema;

@Repository
public class CinemaRepository {

	@Autowired
	private JdbcTemplate jdbc;

	public List<Cinema> findAllActive() {
		String sql = "SELECT * FROM cinema WHERE is_active = 1";
		try {
			return jdbc.query(sql, new CinemaMapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Cinema> findAll() {
		String sql = "SELECT * FROM cinema";
		try {
			return jdbc.query(sql, new CinemaMapper());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Cinema findById(int id) {
		String sql = "SELECT * FROM cinema WHERE id = ?";
		try {
			return jdbc.queryForObject(sql, new CinemaMapper(), id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int insert(Cinema c) {
		String sql = """
				    INSERT INTO cinema(name, address, phone, description, image_url, is_active, created_at)
				    VALUES (?, ?, ?, ?, ?, 1, NOW())
				""";
		try {
			return jdbc.update(sql,
				    c.getName(),
				    c.getAddress(),
				    c.getPhone(),
				    c.getDescription(),  
				    c.getImageUrl()      
				);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int update(int id, Cinema c) {
		String sql = """
				    UPDATE cinema
				    SET name=?, address=?, phone=?, description=?, is_active=?, image_url=?
				    WHERE id=?
				""";
		try {
			return jdbc.update(sql,
				    c.getName(),
				    c.getAddress(),
				    c.getPhone(),
				    c.getDescription(),
				    c.getIsActive(),    
				    c.getImageUrl(),   
				    id
				);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int deactivate(int id) {
		String sql = "UPDATE cinema SET is_active = 0 WHERE id = ?";
		try {
			return jdbc.update(sql, id);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int activate(int id) {
		String sql = "UPDATE cinema SET is_active = 1 WHERE id = ?";
		try {
			return jdbc.update(sql, id);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public void uploadImage (int id, String imageUrl) {
		String sql = "UPDATE cinema SET image_url = ? WHERE id = ?";
		try {
			jdbc.update(sql, imageUrl, id);
		} catch (Exception e) {
			throw new RuntimeException("Failed to update cinema image" + e.getMessage());
		}
	}
}
