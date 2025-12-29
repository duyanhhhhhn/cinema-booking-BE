package CinemaBooking.Group2.repositories;

import java.util.List;

import CinemaBooking.Group2.models.Payment;

public class PaymentRepository implements Icrud<Payment>{
	private static PaymentRepository _instance=null;
	private PaymentRepository() {
		
	}
	public PaymentRepository Instance() {
		if(_instance==null) {
			_instance = new PaymentRepository();
		}
		return _instance;
	}
	@Override
	public List<Payment> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Payment findById(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Payment> search(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int create(Payment item) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int update(Payment item) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int delete(int id) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
