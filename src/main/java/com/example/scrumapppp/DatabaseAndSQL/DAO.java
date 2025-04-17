package com.example.scrumapppp.DatabaseAndSQL;

public interface DAO<T> {
    //Deze methode kun je ook de Datatype en de waarde lengte waar je objecten kunt invullen bepalen.
    T create(T object);
}
