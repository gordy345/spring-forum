package spring_forum.services;

public interface CrudService<T, ID> {

    T findByID(ID id);

    T save(T entity);

    T update(T entity);

    T deleteByID(ID id);
}
