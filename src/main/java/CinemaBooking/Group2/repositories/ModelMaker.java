package CinemaBooking.Group2.repositories;

import java.util.ArrayList;
import java.util.List;

import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.Post;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.models.Voucher;

public class ModelMaker {
	private static ModelMaker _instance = null;
	private ComboRepository comboRep;
	private ProductRepository proRep;
	private PostRepository postRep;
	private VoucherRepository vouchRep;
	private ModelMaker() {
		comboRep = ComboRepository.Instance();
		proRep = ProductRepository.Instance();
		postRep=PostRepository.Instance();
		vouchRep = VoucherRepository.Instance();
		
	}
	public static ModelMaker Instance () {
		if(_instance==null) {
			_instance=new ModelMaker();
		}
		return _instance;
	}
	//Combo
	public List<Combo> getCombo(){
	    List<Combo> item = new ArrayList<>();
		try {
			item = comboRep.getAll();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	public List<Combo> pagingCombo(int page,int size) {
		List<Combo> item = new ArrayList<>();
		try {
			item = comboRep.Paging(page, size);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	public Combo comboInfo(int id) {
		Combo item = new Combo();
		try {
			item = comboRep.findById(id);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	public int CreateCombo(Combo combo) {
		try {
			int rs = comboRep.create(combo);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return 0;
	}
	public int updateCombo(Combo combo) {
		int rs = 0;
		try {
			rs = comboRep.update(combo);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	public int changeStatusCombo(int id,int status) {
		int rs=0;
		try {
			rs = comboRep.ChangeActive(id, status);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	public int deleteCombo(int id) {
		int rs=0;
		try {
			rs = comboRep.delete(id);
		}
		catch (Exception e) {
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
		return null;
	}
	public int createProduct(Product product) {
		return 0;
	}
	public int updateProduct(Product product) {
		return 0;
	}
	public int deleteProduct(int id) {
		return 0;
	}
	//Post
	public List<Post> getPost(){
		List<Post> item = new ArrayList<>();
		try {
			item = postRep.getAll();
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	public int createPost(Post post) {
		try {
			int rs =  postRep.newPost(post);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	//Voucher
	public List<Voucher> getVoucher(){
		List<Voucher> item=new ArrayList<>();
		try {
			item = vouchRep.getAll();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	public int createVoucher(Voucher voucher) {
		int rs=0;
		try {
			rs = vouchRep.create(voucher);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
}
