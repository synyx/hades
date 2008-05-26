package org.synyx.hades.domain;

/**
 * Enumeration for sort orders.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public enum Order {

    ASCENDING("asc"), DESCENDING("desc");

    private String jpaValue;


    private Order(String jpaValue) {

        this.jpaValue = jpaValue;
    }


    /**
     * Returns the JPA specific value.
     * 
     * @return
     */
    public String getJpaValue() {

        return jpaValue;
    }
}
