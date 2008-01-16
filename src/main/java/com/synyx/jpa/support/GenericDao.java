package com.synyx.jpa.support;

import java.io.Serializable;
import java.util.List;


/**
 * Interface for generic CRUD operations on a DAO for a specific type.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public interface GenericDao<T, PK extends Serializable> extends
        FinderExecuter<T> {

    /**
     * Saves a given entity. Use the returned instance for further operations as
     * the save operation might have changed the paramter object's state.
     * 
     * @param entity
     * @return
     */
    public abstract T save(T entity);


    /**
     * Retrives an entity by it's primary key.
     * 
     * @param primaryKey
     * @return
     */
    public abstract T readByPrimaryKey(PK primaryKey);


    /**
     * Returns all instances of the type.
     * 
     * @return
     */
    public abstract List<T> readAll();


    /**
     * Merges a transient entity instance with the current session.
     * 
     * @param transientEntity
     */
    public abstract void merge(T transientEntity);


    /**
     * Deletes a given entity.
     * 
     * @param entity
     */
    public abstract void delete(T entity);
}
