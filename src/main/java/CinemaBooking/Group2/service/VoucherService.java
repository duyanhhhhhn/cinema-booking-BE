package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.VoucherResponseDTO;
import CinemaBooking.Group2.mappers.VoucherMapper;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.pattern.ModelMaker;

@Service
public class VoucherService {
	public List<VoucherResponseDTO> getVoucher(){
		List<Voucher> item =null;
		try {
			item = ModelMaker.Instance().getVoucher();
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
			int rs = ModelMaker.Instance().createVoucher(item);
			if(rs==1) {
				ms = "Success";
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return ms;
	}
}
