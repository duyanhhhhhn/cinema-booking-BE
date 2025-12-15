package CinemaBooking.Group2.repositories;

import java.util.List;

public interface Icrud<T> {
	public List<T> getAll();
	public T findById(int id);
	public List<T> search(String key);
	public int create(T item);
	public int update(T item);
	public int delete(int id);
}
