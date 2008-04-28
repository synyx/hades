package org.synyx.hades.domain;

/**
 * Abstract interface for pagination information.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface Pageable {

    /**
     * Returns the first item to be returned.
     * 
     * @return the first item to be returned
     */
    public abstract int getFirstItem();


    /**
     * Returns the number of items to be returned.
     * 
     * @return the number of items of that page
     */
    public abstract int getNumberOfItems();
}
