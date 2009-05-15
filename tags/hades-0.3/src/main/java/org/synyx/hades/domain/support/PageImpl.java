/*
 * Copyright 2002-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.synyx.hades.domain.support;

import java.util.Iterator;
import java.util.List;

import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;


/**
 * Basic {@code Page} implementation.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <T> the type of which the page consists.
 */
public class PageImpl<T> implements Page<T> {

    private List<T> content;
    private int number;
    private int pageSize;
    private long total;


    /**
     * Constructor of {@code PageImpl}.
     * 
     * @param content
     * @param pageable
     * @param total
     */
    public PageImpl(final List<T> content, final Pageable pageable,
            final long total) {

        if (null == content) {
            throw new IllegalArgumentException("Content must not be null!");
        }

        if (null == pageable) {
            throw new IllegalArgumentException("Pageable must not be null!");
        }

        this.content = content;
        this.number = pageable.getPage();
        this.pageSize = pageable.getNumberOfItems();
        this.total = total;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Page#getNumber()
     */
    public int getNumber() {

        return number;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Page#getTotalPages()
     */
    public int getTotalPages() {

        return (int) Math.ceil(new Long(total).doubleValue()
                / new Integer(pageSize).doubleValue());
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Page#getPageSize()
     */
    public int getPageSize() {

        return pageSize;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Page#getNumberOfElements()
     */
    public int getNumberOfElements() {

        return content.size();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Page#getTotalElements()
     */
    public long getTotalElements() {

        return total;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Page#hasPerviousPage()
     */
    public boolean hasPreviousPage() {

        return number > 0;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Page#hasNextPage()
     */
    public boolean hasNextPage() {

        return ((number + 1) * pageSize) < total;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Page#iterator()
     */
    public Iterator<T> iterator() {

        return content.iterator();
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        String contentType = "UNKNOWN";

        if (content.size() > 0) {
            contentType = content.get(0).getClass().getName();
        }

        return String.format("Page %s of %d containing %s instances", number,
                getTotalPages(), contentType);
    }
}
