package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.VoucherResponseDTO;
import CinemaBooking.Group2.dtos.marketing.PostResponseDTO;
import CinemaBooking.Group2.mappers.PostMapper;
import CinemaBooking.Group2.mappers.VoucherMapper;
import CinemaBooking.Group2.models.Post;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.pattern.Marketing;

@Service
public class MarketingService {
	public List<PostResponseDTO> getAllPost() {
		try {
			List<Post> item = Marketing.Instance().getPost();
			return item.stream().map(PostMapper::toResponseDTO).collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
	public String newPost(Post post) {
		String s = "Failed to Create New Post";
		try {
			int rs=Marketing.Instance().createPost(post);
			if(rs==1) {
				s="Success";
			}
			return s;
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
	public List<VoucherResponseDTO> getVoucher(){
		List<Voucher> item =null;
		try {
			item = Marketing.Instance().getVoucher();
			return item.stream().map(VoucherMapper::toResponseDTO)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public String checkVoucher(int id) {
		try {
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public String addVoucher(Voucher item) {
		String ms = "Failed";
		try {
			int rs = Marketing.Instance().createVoucher(item);
			if(rs==1) {
				ms = "Success";
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ms;
	}
	public PostResponseDTO postInfo(int id) {
		try {
			return PostMapper.toResponseDTO(Marketing.Instance().postInfo(id));
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public BigDecimal checkDiscount(int id,BigDecimal price) {
		try {
			return Marketing.Instance().checkPrice(id, price);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
