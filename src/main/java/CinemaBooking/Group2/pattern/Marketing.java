package CinemaBooking.Group2.pattern;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.models.Post;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.repositories.marketing.PostRepository;
import CinemaBooking.Group2.repositories.marketing.VoucherRepository;

@Repository
public class Marketing {
	@Autowired
	private VoucherRepository vouchRep;
	@Autowired
	private PostRepository postRep;
	public Marketing() {
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
	public int getPostCount() {
		try {
			return postRep.getPostCount();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	public List<Post> getPost(int page,int size){
		List<Post> item = new ArrayList<>();
		try {
				item = postRep.Paging(page, size);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	public Post postInfo(int id) {
		try {
			Post item = postRep.findById(id);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
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
	public Voucher voucherInfo(int id) {
		Voucher item = new Voucher();
		try {
			item = vouchRep.findById(id);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public Voucher voucherInfoByCode(String code) {
		Voucher item = new Voucher();
		try {
			item = vouchRep.findByCode(code);
			return item;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public List<Voucher> getVoucher(int page,int size){
		List<Voucher> item = new ArrayList<>();
		try {
			item = vouchRep.Paging(page, size);
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
	public int updateVoucher(Voucher voucher) {
		int rs=0;
		try {
			rs=vouchRep.update(voucher);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	public int deleteVoucher(int id) {
		int rs=0;
		try {
			rs = vouchRep.delete(id);
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