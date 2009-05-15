package org.synyx.hades.domain;

import javax.persistence.Entity;

import org.synyx.hades.domain.support.AbstractPersistable;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
@Entity
public class Account extends AbstractPersistable<Long> {

    private static final long serialVersionUID = -5719129808165758887L;
}
