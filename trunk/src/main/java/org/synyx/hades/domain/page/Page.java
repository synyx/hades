package org.synyx.hades.domain.page;

import java.util.Iterator;


/**
 * A page is a sublist of a list of objects. It allows gain information about
 * the position of it in the containing entire list.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <T>
 */
public interface Page<T> extends Iterable<T> {

    /**
     * Returns the number of the current page. Is always greater than zero and
     * less that {@code Page#getTotalPages()}.
     * 
     * @return the number of the current page
     */
    int getNumber();


    /**
     * Returns the number of total pages.
     * 
     * @return the number of toral pages
     */
    int getTotalPages();


    /**
     * Returns the size of the page.
     * 
     * @return the size of the page
     */
    int getPageSize();


    /**
     * Returns the number of elements currently on this page.
     * 
     * @return the number of elements currently on this page
     */
    int getNumberOfElements();


    /**
     * Returns the total amount of elements.
     * 
     * @return the total amount of elements
     */
    long getTotalElements();


    /**
     * Returns if there is a previous page.
     * 
     * @return if there is a previous page
     */
    boolean hasPreviousPage();


    /**
     * Returns if there is a next page.
     * 
     * @return if there is a next page
     */
    boolean hasNextPage();


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    Iterator<T> iterator();
}