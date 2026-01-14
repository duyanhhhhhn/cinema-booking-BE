package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.ComboCRUDResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.mappers.ComboMapper;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.Enum.ResponseStatus;
import CinemaBooking.Group2.pattern.Concessions;
import CinemaBooking.Group2.repositories.UserRepository;

@Service
public class ComboService {
	@Autowired private UserRepository rep;
	@Autowired private Concessions con;
	public List<ComboResponseDTO> getCombo(){
		try {
			List<Combo> item =  con.getCombo();
			return item.stream().map(ComboMapper::toResponseDTO)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
	public List<Combo> getCombo(int page,int size ){
		try {
			List<Combo> item =  con.pagingCombo(page, size);
			return item;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
			// TODO: handle exception
		}
	}
	public ComboResponseDTO comboInfo(int id){
		try {
			Combo item =  con.comboInfo(id);
			return ComboMapper.toResponseDTO(item);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
			// TODO: handle exception
		}
	}
	public ComboCRUDResponseDTO AddCombo(Combo combo) {
		ComboCRUDResponseDTO res= new ComboCRUDResponseDTO();
		try {
			int rs =  con.CreateCombo(combo);
			if(rs==1) {
				res.setMessage("Success");
				res.setStatus(ResponseStatus.SUCCESS);
			}
			else if(rs==0) {
				res.setMessage("Error");
				res.setStatus(ResponseStatus.ERROR);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}
	public String EditCombo(Combo combo) {
		String s = "Error";
		try {
			int rs =  con.updateCombo(combo);
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
		return  con.deleteCombo(id);
	}
	public int ChangeActive(int id,int active) {
		return  con.changeStatusCombo(id, active);
	}
	public int checkAdmin(int id) {
		try {
			User user = rep.findById(id);
			if(user.getRoleId()==1) {
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
