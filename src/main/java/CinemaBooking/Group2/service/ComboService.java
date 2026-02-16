package CinemaBooking.Group2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.CnPResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboCRUDResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboItemResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.mappers.ComboItemMapper;
import CinemaBooking.Group2.mappers.ComboMapper;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.ComboItem;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.Enum.ResponseStatus;
import CinemaBooking.Group2.pattern.Concessions;
import CinemaBooking.Group2.repositories.UserRepository;

@Service
public class ComboService {
	@Autowired private UserRepository rep;
	@Autowired private Concessions con;
	@Autowired private ComboItemMapper mapper;
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
	public List<CnPResponseDTO> getCombo(int page,int size ){
		try {
			List<Combo> item =  con.pagingCombo(page, size);
			List<Product> item1= con.getProduct(page, size);
			List<CnPResponseDTO> list = new ArrayList<>();
			for (Combo combo : item) {
				CnPResponseDTO com =  new CnPResponseDTO();
				com.setId(combo.getId());
				com.setDescription(combo.getDescription());
				com.setImageUrl(combo.getImageUrl());
				com.setName(combo.getName());
				com.setPrice(combo.getPrice());
				com.setIsActive(combo.getIsActive());
				com.setStock(calculateComboStock(combo.getId()));
				com.setType("COMBO");
				List<ComboItem> cbitem = new ArrayList<>();
				cbitem = con.getComboItem(combo.getId());
				if(cbitem!=null) {
					List<ComboItemResponseDTO>  res = new ArrayList<>();
					for(ComboItem cbi : cbitem) {
						
						res.add(mapper.toResponseDTO(cbi));
					}
					com.setItemList(res);
				}
				list.add(com);
			}
			for(Product pro : item1) {
				CnPResponseDTO com =  new CnPResponseDTO();
				com.setId(pro.getId());
				com.setDescription(pro.getDescription());
				com.setImageUrl(pro.getImageUrl());
				com.setName(pro.getName());
				com.setPrice(pro.getPrice());
				com.setIsActive(pro.getIsActive());
				com.setStock(pro.getStock());
				com.setType("SINGLE");
				list.add(com);
			}
			return list;
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
	public int calculateComboStock(int id ) {
		try {
			List<ComboItem> list = con.getComboItem(id);
			int stock = Integer.MAX_VALUE;
			for(ComboItem item : list){
				int pro_stock = con.productInfo(item.getProductId()).getStock();
				int item_stock = item.getQuantity();
				int possible = pro_stock / item_stock;
				stock = Math.min(stock,possible);
			}
			return stock;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
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
