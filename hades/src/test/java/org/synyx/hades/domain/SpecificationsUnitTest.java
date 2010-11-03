/*
 * Copyright 2008-2010 the original author or authors.
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
package org.synyx.hades.domain;

import static org.mockito.Mockito.*;
import static org.synyx.hades.domain.Specifications.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link Specifications} class.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class SpecificationsUnitTest {

    @Mock
    Root<Object> root;

    @Mock
    CriteriaQuery<?> query;

    @Mock
    CriteriaBuilder cb;

    @Mock
    Predicate predicateOne, predicateTwo, predicateThree, intermediate;
    Specification<Object> first, second, third;


    @Before
    public void setUp() {

        first = spec(predicateOne);
        second = spec(predicateTwo);
        third = spec(predicateThree);
    }


    @Test
    public void doesSingleChainingCorrectly() throws Exception {

        Specifications<Object> spec = where(first).and(second);
        spec.toPredicate(root, query, cb);

        verify(cb, times(1)).and(predicateOne, predicateTwo);
    }


    @Test
    public void doesExtendedChainingCorrectlyAndOr() throws Exception {

        when(cb.and(predicateOne, predicateTwo)).thenReturn(intermediate);

        Specifications<Object> spec = where(first).and(second).or(third);
        spec.toPredicate(root, query, cb);

        verify(cb, times(1)).and(predicateOne, predicateTwo);
        verify(cb, times(1)).or(intermediate, predicateThree);
    }


    @Test
    public void doesExtendedChainingCorrectlyAndAnd() throws Exception {

        when(cb.and(predicateOne, predicateTwo)).thenReturn(intermediate);

        Specifications<Object> spec = where(first).and(second).and(third);
        spec.toPredicate(root, query, cb);

        verify(cb, times(1)).and(predicateOne, predicateTwo);
        verify(cb, times(1)).and(intermediate, predicateThree);
    }


    @Test
    public void doesExtendedChainingCorrectlyOrAnd() throws Exception {

        when(cb.or(predicateOne, predicateTwo)).thenReturn(intermediate);

        Specifications<Object> spec = where(first).or(second).and(third);
        spec.toPredicate(root, query, cb);

        verify(cb, times(1)).or(predicateOne, predicateTwo);
        verify(cb, times(1)).and(intermediate, predicateThree);
    }


    private static Specification<Object> spec(final Predicate pred) {

        return new Specification<Object>() {

            public Predicate toPredicate(Root<Object> root,
                    CriteriaQuery<?> query, CriteriaBuilder builder) {

                return pred;
            }
        };
    }
}
