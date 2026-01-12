package CinemaBooking.Group2.pattern;

import java.util.ArrayList;
import java.util.List;

import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.repositories.ComboRepository;
import CinemaBooking.Group2.repositories.ProductRepository;

public class Concessions {
	private static Concessions _instance = null;
	private ProductRepository proRep;
	private ComboRepository comboRep;

	private Concessions() {
		comboRep = ComboRepository.Instance();
		proRep= ProductRepository.Instance();
	}

	public static Concessions Instance() {
		if (_instance == null) {
			_instance = new Concessions();
		}
		return _instance;
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
