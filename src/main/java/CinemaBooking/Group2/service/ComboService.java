package CinemaBooking.Group2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.mappers.ComboMapper;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.repositories.ComboRepository;

@Service
public class ComboService {
	@Autowired
	ComboRepository rep;
	public List<ComboResponseDTO> getCombo(){
		try {
			List<Combo> item = rep.getAll();
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
			List<Combo> item = rep.Paging(page, size);
			return item.stream().map(ComboMapper::toResponseDTO)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
			// TODO: handle exception
		}
	}
	public List<ComboResponseDTO> comboInfo(int id){
		try {
			List<Combo> item = rep.findById(id);
			return item.stream().map(ComboMapper::toResponseDTO)
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
			// TODO: handle exception
		}
	}
	public int AddCombo(Combo combo) {
		return rep.Create(combo);
	}
	public int EditCombo(Combo combo) {
		return rep.Update(combo);
	}
	public int DeleteCombo(int id) {
		return rep.Delete(id);
	}
	public int ChangeActive(int id,int active) {
		return rep.ChangeActive(id, active);
	}
}
