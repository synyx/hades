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
     * Creates a new {@code PageRequest}. Pages are zero indexed, thus providing
     * 0 for {@code page} will return the first page.
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


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.page.Pageable#getFirstItem()
     */
    public int getFirstItem() {

        return page * size;
    }
}
