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

import junit.framework.TestCase;

import org.synyx.hades.domain.support.PageRequest;


/**
 * Unit test for {@code PageRequest}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PageRequestUnitTest extends TestCase {

    /**
     * Tests, that the request can not be instantiated with a negative page.
     */
    public void testRejectsInvalidPage() {

        assertIllegalArgumentException(-1, 4);
    }


    /**
     * Tests, that the request can not be instantiated with a negative size.
     */
    public void testRejectsInvalidSize() {

        assertIllegalArgumentException(4, -1);
    }


    /**
     * Tests, that instantiating a {@code PageRequest} with the given values
     * throws an {@code IllegalArgumentException}.
     * 
     * @param page
     * @param size
     */
    private void assertIllegalArgumentException(int page, int size) {

        try {
            new PageRequest(page, size);
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }
    }
}
