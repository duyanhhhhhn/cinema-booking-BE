package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.VoucherResponseDTO;
import CinemaBooking.Group2.dtos.marketing.ListPostResponseDTO;
import CinemaBooking.Group2.dtos.marketing.PostResponseDTO;
import CinemaBooking.Group2.mappers.PostMapper;
import CinemaBooking.Group2.mappers.VoucherMapper;
import CinemaBooking.Group2.models.Post;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.pattern.Marketing;

@Service
public class MarketingService {
	@Autowired private Marketing mk;
	public ListPostResponseDTO getAllPost() {
		try {
			List<Post> item = mk.getPost();
			ListPostResponseDTO list = new ListPostResponseDTO();
			if(item==null) {
				list.setIs_success(false);
				list.setMessage("Not found");
			}
			
			list.setList(item);
			list.setIs_success(true);
			list.setMessage("Success");
			return list;
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
	public ListPostResponseDTO getPostPaging(int page,int size) {
		try {
			List<Post> item = mk.getPost(page,size);
			ListPostResponseDTO list = new ListPostResponseDTO();
			if(item==null) {
				list.setIs_success(false);
				list.setMessage("Not found");
			}
			
			list.setList(item);
			list.setIs_success(true);
			list.setMessage("Success");
			return list;
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
	public PostResponseDTO newPost(Post post) {
		PostResponseDTO res = new PostResponseDTO();
		try {
			int rs=mk.createPost(post);
			if(rs==1) {
				res = PostMapper.toResponseDTO(post);
				res.setMessage("Created ");
				res.setIsSuccess(true);
			}
			else if(rs==0){
				res.setMessage("Error");
				res.setIsSuccess(false);
			}
			return res;
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException();
		}
	}
	public List<VoucherResponseDTO> getVoucher(){
		List<Voucher> item =null;
		try {
			item = mk.getVoucher();
			return item.stream().map(VoucherMapper::toResponseDTO)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public List<VoucherResponseDTO> getVoucherPaging(int page,int size){
		List<Voucher> item =null;
		try {
			item = mk.getVoucher(page,size);
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
	public VoucherResponseDTO addVoucher(Voucher item) {
		VoucherResponseDTO dto = new VoucherResponseDTO();
		try {
			int rs = mk.createVoucher(item);
			if(rs==1) {
				dto = VoucherMapper.toResponseDTO(item);
				dto.setMessage("Success");
				dto.setIsSuccess(true);
				return dto;
			}
			dto.setMessage("failed");
			dto.setIsSuccess(false);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return dto;
	}
	public PostResponseDTO postInfo(int id) {
		try {
			return PostMapper.toResponseDTO(mk.postInfo(id));
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public VoucherResponseDTO checkDiscount(int id,BigDecimal price) {
		VoucherResponseDTO dto = new VoucherResponseDTO();
		try {
			if(id==0) {
				dto.setMessage("Id is shouldn't be 0");
				dto.setIsSuccess(false);
				return dto;
			}
			BigDecimal rs = mk.checkPrice(id, price);
			if(rs==BigDecimal.valueOf(0)) {
				return null;
			}
			dto.setDiscountPrice(rs);
			dto.setMessage("Success");
			dto.setIsSuccess(true);
			return dto;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return dto;
	}
}
