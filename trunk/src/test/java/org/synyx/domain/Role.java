package org.synyx.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * @author Oliver Gierke
 */
@Entity
public class Role implements org.synyx.domain.Identifyable<Integer> {

    private static final String PREFIX = "ROLE_";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;


    /**
     * 
     */
    public Role() {

        // TODO Auto-generated constructor stub
    }


    /**
     * @param name
     */
    public Role(String name) {

        this.name = name;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.jpa.support.Entity#getId()
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
     * @see org.synyx.jpa.support.Entity#isNew()
     */
    public boolean isNew() {

        return id != null;
    }
}
