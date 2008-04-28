package org.synyx.dao;

import java.io.Serializable;
import java.util.List;

import org.synyx.domain.Identifyable;
import org.synyx.domain.Pageable;


/**
 * Interface for generic CRUD operations on a DAO for a specific type.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface GenericDao<T extends Identifyable<PK>, PK extends Serializable>
        extends FinderExecuter<T> {

    /**
     * Saves a given entity. Use the returned instance for further operations as
     * the save operation might have changed the entity instance completely.
     * 
     * @param entity
     * @return the saved entity
     */
    public abstract T save(T entity);


    /**
     * Saves an entity and flushes changes instantly to the database.
     * 
     * @param entity
     * @return the saved entity
     */
    public abstract T saveAndFlush(T entity);


    /**
     * Retrives an entity by it's primary key.
     * 
     * @param primaryKey
     * @return the entity with the given primary key
     * @throws IllegalArgumentException if primaryKey is null
     */
    public abstract T readByPrimaryKey(PK primaryKey);


    /**
     * Returns all instances of the type.
     * 
     * @return all entities
     */
    public abstract List<T> readAll();


    /**
     * Returns a paged list of entities meeting the paging restriction provided
     * in the {@code Pageable} object.
     * 
     * @param pageable
     * @return a page of entities
     */
    public abstract List<T> readAll(Pageable pageable);


    /**
     * Returns the number of entities available.
     * 
     * @return the number of entities
     */
    public abstract Long count();


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
