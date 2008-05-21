package org.synyx.hades.domain.page;

/**
 * Abstract interface for pagination information.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface Pageable {

    /**
     * Returns the page to be returned.
     * 
     * @return the page to be returned.
     */
    int getPage();


    /**
     * Returns the number of items to be returned.
     * 
     * @return the number of items of that page
     */
    int getNumberOfItems();
}
