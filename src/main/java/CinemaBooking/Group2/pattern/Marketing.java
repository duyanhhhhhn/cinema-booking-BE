package CinemaBooking.Group2.pattern;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import CinemaBooking.Group2.models.Post;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.repositories.PostRepository;
import CinemaBooking.Group2.repositories.VoucherRepository;

public class Marketing {
	private static Marketing _instance = null;
	private VoucherRepository vouchRep;
	private PostRepository postRep;
	private Marketing() {
		vouchRep = VoucherRepository.Instance();
		postRep = PostRepository.Instance();
	}
	public static Marketing Instance() {
		if(_instance==null) {
			_instance=new Marketing();
		}
		return _instance;
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
	public int checkVoucher(int id) {
		try {
		   boolean rs = vouchRep.checkAvailableVoucher(id);
		   if(rs) {
			   return 1;
		   }
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public BigDecimal checkPrice(int id,BigDecimal price) {
		BigDecimal rs = BigDecimal.valueOf(0);
		try {
			rs = vouchRep.checkDiscount(id, price);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
}
