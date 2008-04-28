package org.synyx.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


/**
 * Abstract base class for entities. Allows parameterization of id type and
 * offers a flag determining the "new" state of the entity.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@MappedSuperclass
public class AbstractEntity<PK extends Serializable> implements
        Identifyable<PK> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private PK id;


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.jpa.support.Entity#getId()
     */
    public PK getId() {

        return id;
    }


    /**
     * Sets the id of the entity.
     * 
     * @param id the id to set
     */
    public void setId(PK id) {

        this.id = id;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.jpa.support.Entity#isNew()
     */
    public boolean isNew() {

        return null == getId();
    }
}