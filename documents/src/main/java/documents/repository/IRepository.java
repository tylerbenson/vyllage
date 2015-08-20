package documents.repository;

import java.util.List;

public interface IRepository<T> {

	public abstract T get(Long id) throws ElementNotFoundException;

	public abstract List<T> getAll();

	public abstract T save(T t);

	public abstract void delete(Long id);

}
