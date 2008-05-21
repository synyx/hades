package org.synyx.hades.domain;

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
 * @param <PK> the tpe of the entity
 */
@MappedSuperclass
@SuppressWarnings("serial")
public class AbstractPersistable<PK extends Serializable> implements
        Persistable<PK> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private PK id;


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.Entity#getId()
     */
    public PK getId() {

        return id;
    }


    /**
     * Sets the id of the entity.
     * 
     * @param id the id to set
     */
    public void setId(final PK id) {

        this.id = id;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.Entity#isNew()
     */
    public boolean isNew() {

        return null == getId();
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "Entity of type " + this.getClass().getName() + " with id: "
                + getId();
    }
}