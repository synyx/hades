package org.synyx.jpa.support.test.dao;

import java.util.List;

import org.synyx.jpa.support.GenericDao;
import org.synyx.jpa.support.test.domain.Person;



/**
 * DAO Interface for <code>Person</code>s.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public interface PersonDao extends GenericDao<Person, Integer> {

    /**
     * Retrieve persons by name.
     * 
     * @param name
     * @return
     */
    public List<Person> findByName(String name);
}
