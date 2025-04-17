package com.example.scrumapppp.DatabaseAndSQL;

public interface CrudRepository<T> {
    public T create(T entity);
    public void update(T entity);
    public void delete(int id);
}
