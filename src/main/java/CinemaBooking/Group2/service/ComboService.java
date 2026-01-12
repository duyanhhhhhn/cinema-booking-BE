package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.mappers.ComboMapper;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.pattern.Concessions;
import CinemaBooking.Group2.repositories.UserRepository;

@Service
public class ComboService {
	@Autowired
	private UserRepository rep;
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
	public String AddCombo(Combo combo) {
		String m="Error";
		try {
			int rs = Concessions.Instance().CreateCombo(combo);
			if(rs==1) {
				m = "Success";
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return m;
	}
	public String EditCombo(Combo combo) {
		String s = "Error";
		try {
			int rs = Concessions.Instance().updateCombo(combo);
			if(rs==1) {
				s="Edit Success";
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
	public int checkAdmin(int id) {
		try {
			User user = rep.findById(id);
			if(user.getRoleId()==0) {
				return 1;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}
}
