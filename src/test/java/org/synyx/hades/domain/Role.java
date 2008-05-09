package org.synyx.hades.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * Example implementation of the very basic {@code Entity} interface. The id
 * type is matching the typisation of the interface. {@code Entity#isNew()} is
 * implemented regarding the id as flag.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@javax.persistence.Entity
public class Role implements Persistable<Integer> {

    private static final long serialVersionUID = -8832631113344035104L;

    private static final String PREFIX = "ROLE_";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;


    /**
     * Creates a new instance of {@code Role}.
     */
    public Role() {

    }


    /**
     * Creates a new preconfigured {@code Role}.
     * 
     * @param name
     */
    public Role(String name) {

        this.name = name;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.jpa.support.Entity#getId()
     */
    public Integer getId() {

        return id;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return PREFIX + name;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.jpa.support.Entity#isNew()
     */
    public boolean isNew() {

        return id != null;
    }
}
