package CinemaBooking.Group2.pattern;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.ComboItem;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.repositories.ComboItemRepository;
import CinemaBooking.Group2.repositories.ComboRepository;
import CinemaBooking.Group2.repositories.ProductRepository;

@Repository
public class Concessions {
	@Autowired
	private ProductRepository proRep;
	@Autowired
	private ComboRepository comboRep;
	@Autowired
	private ComboItemRepository itemRep;
	public Concessions() {
	}

	// Combo
	public List<Combo> getCombo() {
		List<Combo> item = new ArrayList<>();
		try {
			item = comboRep.getAll();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}

	public List<Combo> pagingCombo(int page, int size) {
		List<Combo> item = new ArrayList<>();
		try {
			item = comboRep.Paging(page, size);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}

	public Combo comboInfo(int id) {
		Combo item = new Combo();
		try {
			item = comboRep.findById(id);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}

	public int CreateCombo(Combo combo) {
		try {
			int rs = comboRep.create(combo);
			return rs;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return 0;
	}

	public int updateCombo(Combo combo) {
		int rs = 0;
		try {
			rs = comboRep.update(combo);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}

	public int changeStatusCombo(int id, int status) {
		int rs = 0;
		try {
			rs = comboRep.ChangeActive(id, status);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	public List<ComboItem> getComboItem(int id){
		List<ComboItem> item = new ArrayList<>();
		try {
			item = itemRep.getByCombo(id);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}

	public int deleteCombo(int id) {
		int rs = 0;
		try {
			rs = comboRep.delete(id);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	//Product
	public List<Product> getProduct(){
		try {
			List<Product> item = proRep.getAll();
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return null;
	}
	public List<Product> getProduct(int page,int size){
		try {
			List<Product> item = proRep.getAll(page,size);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return null;
	}
	public Product productInfo(int id) {
		try {
			Product item = proRep.findById(id);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public int createProduct(Product product) {
		try {
			int rs = proRep.create(product);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public int updateProduct(Product product) {
		return 0;
	}
	public int deleteProduct(int id) {
		return 0;
	}
}
