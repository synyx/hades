/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.synyx.hades.domain.page;

import java.util.Iterator;
import java.util.List;


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
     * @param number
     * @param pageSize
     * @param total
     */
    public PageImpl(final List<T> content, final int number,
            final int pageSize, final long total) {

        if (null == content) {
            throw new IllegalArgumentException("Content must not be null!");
        }

        if (number < 0) {
            throw new IllegalArgumentException(
                    "Page number must not be less than zero!");
        }

        if (pageSize < 0) {
            throw new IllegalArgumentException(
                    "Page size must not be less than zero");
        }

        this.content = content;
        this.number = number;
        this.pageSize = pageSize;
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

        // TODO Auto-generated method stub
        return 0;
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

        return "Page " + number + " of " + getTotalPages() + " " + contentType;
    }
}
