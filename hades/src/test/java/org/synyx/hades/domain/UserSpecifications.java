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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * Collection of {@link Specification}s for a {@link User}.
 * 
 * @author Oliver Gierke
 */
public class UserSpecifications {

    /**
     * A {@link Specification} to match on a {@link User}'s firstname.
     * 
     * @param firstname
     * @return
     */
    public static Specification<User> userHasFirstname(final String firstname) {

        return simplePropertySpec("firstname", firstname);
    }


    /**
     * A {@link Specification} to match on a {@link User}'s lastname.
     * 
     * @param firstname
     * @return
     */
    public static Specification<User> userHasLastname(final String lastname) {

        return simplePropertySpec("lastname", lastname);
    }


    private static <T> Specification<T> simplePropertySpec(
            final String property, final Object value) {

        return new Specification<T>() {

            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
                    CriteriaBuilder builder) {

                return builder.equal(root.get(property), value);
            }
        };
    }
}