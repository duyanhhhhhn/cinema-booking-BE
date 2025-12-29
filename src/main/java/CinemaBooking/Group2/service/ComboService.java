package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.mappers.ComboMapper;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.pattern.Concessions;

@Service
public class ComboService {
	public List<ComboResponseDTO> getCombo(){
		try {
			List<Combo> item = Concessions.Instance().getCombo();
			return item.stream().map(ComboMapper::toResponseDTO)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
	public List<ComboResponseDTO> getCombo(int page,int size ){
		try {
			List<Combo> item = Concessions.Instance().pagingCombo(page, size);
			return item.stream().map(ComboMapper::toResponseDTO)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
			// TODO: handle exception
		}
	}
	public ComboResponseDTO comboInfo(int id){
		try {
			Combo item = Concessions.Instance().comboInfo(id);
			return ComboMapper.toResponseDTO(item);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
			// TODO: handle exception
		}
	}
	public int AddCombo(Combo combo) {
		int rs=0;
		try {
			rs = Concessions.Instance().CreateCombo(combo);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	public String EditCombo(Combo combo) {
		String s = "Error";
		try {
			int rs = Concessions.Instance().updateCombo(combo);
			if(rs==1) {
				s="Success";
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return s;
	}
	public int DeleteCombo(int id) {
		return Concessions.Instance().deleteCombo(id);
	}
	public int ChangeActive(int id,int active) {
		return Concessions.Instance().changeStatusCombo(id, active);
	}
}
