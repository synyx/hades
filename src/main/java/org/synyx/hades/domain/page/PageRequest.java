package org.synyx.hades.domain.page;

/**
 * Basic Java Bean implementation of {@code Pageable}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PageRequest implements Pageable {

    private int page;
    private int size;


    /**
     * Creates a new {@code PageRequest}.
     * 
     * @param size
     * @param page
     */
    public PageRequest(final int page, final int size) {

        if (0 > page) {
            throw new IllegalArgumentException(
                    "Page index must not be less than zero!");
        }

        if (0 > size) {
            throw new IllegalArgumentException(
                    "Page size must not be less than zero!");
        }

        this.page = page;
        this.size = size;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Pageable#getNumberOfItems()
     */
    public int getNumberOfItems() {

        return size;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Pageable#getPage()
     */
    public int getPage() {

        return page;
    }
}
