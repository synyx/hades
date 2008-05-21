package org.synyx.hades.dao;

import java.io.Serializable;
import java.util.List;

import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.page.Page;
import org.synyx.hades.domain.page.Pageable;


/**
 * Interface for generic CRUD operations on a DAO for a specific type.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface GenericDao<T extends Persistable<PK>, PK extends Serializable>
        extends FinderExecuter<T> {

    /**
     * Saves a given entity. Use the returned instance for further operations as
     * the save operation might have changed the entity instance completely.
     * 
     * @param entity
     * @return the saved entity
     */
    T save(final T entity);


    /**
     * Saves an entity and flushes changes instantly to the database.
     * 
     * @param entity
     * @return the saved entity
     */
    T saveAndFlush(final T entity);


    /**
     * Retrives an entity by it's primary key.
     * 
     * @param primaryKey
     * @return the entity with the given primary key
     * @throws IllegalArgumentException if primaryKey is null
     */
    T readByPrimaryKey(final PK primaryKey);


    /**
     * Returns all instances of the type.
     * 
     * @return all entities
     */
    List<T> readAll();


    /**
     * Returns a paged list of entities meeting the paging restriction provided
     * in the {@code Pageable} object.
     * 
     * @param pageable
     * @return a page of entities
     */
    Page<T> readAll(final Pageable pageable);


    /**
     * Returns the number of entities available.
     * 
     * @return the number of entities
     */
    Long count();


    /**
     * Deletes a given entity.
     * 
     * @param entity
     */
    void delete(final T entity);


    /**
     * Flushes all pending changes to the database.
     */
    void flush();
}
