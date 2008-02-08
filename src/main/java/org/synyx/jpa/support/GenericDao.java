package org.synyx.jpa.support;

import java.io.Serializable;
import java.util.List;


/**
 * Interface for generic CRUD operations on a DAO for a specific type.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public interface GenericDao<T extends Entity<PK>, PK extends Serializable>
        extends FinderExecuter<T> {

    /**
     * Saves a given entity. Use the returned instance for further operations as
     * the save operation might have changed the paramter object's state.
     * 
     * @param entity
     * @return
     */
    public abstract T save(T entity);


    /**
     * Saves an entity and flushes changes instantly to the database.
     * 
     * @param entity
     * @return
     */
    public abstract T saveAndFlush(T entity);


    /**
     * Retrives an entity by it's primary key.
     * 
     * @param primaryKey
     * @return
     * @throws IllegalArgumentException if primaryKey is null
     */
    public abstract T readByPrimaryKey(PK primaryKey);


    /**
     * Returns all instances of the type.
     * 
     * @return
     */
    public abstract List<T> readAll();


    /**
     * Deletes a given entity.
     * 
     * @param entity
     */
    public abstract void delete(T entity);


    /**
     * Flushes all pending changes to the database.
     */
    public abstract void flush();
}
