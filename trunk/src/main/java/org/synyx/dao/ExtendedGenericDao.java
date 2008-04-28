package org.synyx.dao;

import java.io.Serializable;
import java.util.List;

import org.synyx.domain.Identifyable;
import org.synyx.domain.Pageable;


/**
 * Interface for a more sophisticated DAO implementation. Mostly the
 * functionality declared here will require an implementation that is based of
 * some proprietary features of certain JPA providers.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface ExtendedGenericDao<T extends Identifyable<PK>, PK extends Serializable>
        extends GenericDao<T, PK> {

    /**
     * Returns all entities matching the given examples. If you provide more
     * than one example their restrictions will be OR concatenated. If you
     * provide no example at all or {@code null}, the call returns the same
     * entities as {@code GenericDao#readAll()}.
     * 
     * @param examples
     * @return all objects meeting the criterias expressed by the given examples
     */
    public abstract List<T> readByExample(T... examples);


    /**
     * Allows pageable access to all entities matching the given examples. If
     * you provide {@code null} for the pageable, the call is identical to
     * {@code ExtendedGenericDao#readByExample(T...)}.
     * 
     * @param pageable
     * @param examples
     * @return the page of objects meeting the example's criterias
     */
    public abstract List<T> readByExample(Pageable pageable, T... examples);
}
