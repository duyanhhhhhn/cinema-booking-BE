package CinemaBooking.Group2.repositories;

import java.util.ArrayList;
import java.util.List;

import CinemaBooking.Group2.models.Combo;

public class ModelMaker {
	private static ModelMaker _instance = null;
	private ComboRepository comboRep;
	private ModelMaker() {
		comboRep = ComboRepository.Instance();
	}
	public static ModelMaker Instance () {
		if(_instance==null) {
			_instance=new ModelMaker();
		}
		return _instance;
	}
	public List<Combo> getCombo(){
	    List<Combo> item = new ArrayList<>();
		try {
			item = comboRep.getAll();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	public List<Combo> pagingCombo(int page,int size) {
		List<Combo> item = new ArrayList<>();
		try {
			item = comboRep.Paging(page, size);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	public Combo comboInfo(int id) {
		Combo item = new Combo();
		try {
			item = comboRep.findById(id);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return item;
	}
	public int CreateCombo(Combo combo) {
		try {
			int rs = comboRep.create(combo);
			return rs;
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return 0;
	}
	public int updateCombo(Combo combo) {
		int rs = 0;
		try {
			rs = comboRep.update(combo);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	public int changeStatusCombo(int id,int status) {
		int rs=0;
		try {
			rs = comboRep.ChangeActive(id, status);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	public int deleteCombo(int id) {
		int rs=0;
		try {
			rs = comboRep.delete(id);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
}
