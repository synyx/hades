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
package org.synyx.hades.dao.query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.Assert;
import org.synyx.hades.dao.Param;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;


/**
 * Abstracts method parameters that have to be bound to query parameters or
 * applied to the query independently.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
class Parameters implements Iterable<Class<?>> {

    @SuppressWarnings("unchecked")
    static final List<Class<?>> TYPES =
            Arrays.asList(Pageable.class, Sort.class);

    private static final String PARAM_ON_SPECIAL =
            String.format(
                    "You must not user @%s on a parameter typed %s or %s",
                    Param.class.getSimpleName(),
                    Pageable.class.getSimpleName(), Sort.class.getSimpleName());

    private static final String ALL_OR_NOTHING =
            String
                    .format(
                            "Either use @%s "
                                    + "on all parameters except %s and %s typed once, or none at all!",
                            Param.class.getSimpleName(), Pageable.class
                                    .getSimpleName(), Sort.class
                                    .getSimpleName());

    private int pageableIndex;
    private int sortIndex;

    private Method method;


    /**
     * Creates a new instance of {@link Parameters}.
     * 
     * @param method
     */
    public Parameters(Method method) {

        Assert.notNull(method);
        this.method = method;

        List<Class<?>> types = Arrays.asList(method.getParameterTypes());
        this.pageableIndex = types.indexOf(Pageable.class);
        this.sortIndex = types.indexOf(Sort.class);

        assertEitherAllParamAnnotatedOrNone();
    }


    /**
     * Returns the name of the parameter (through {@link Param} annotation) or
     * null if none can be found.
     * 
     * @param position
     * @return
     */
    public String getParameterName(int position) {

        if (position > method.getParameterAnnotations().length - 1) {
            return null;
        }

        Annotation[] parameterAnnotations =
                method.getParameterAnnotations()[position];

        for (Annotation annotation : parameterAnnotations) {

            if (annotation instanceof Param) {
                return ((Param) annotation).value();
            }
        }

        return null;
    }


    /**
     * Returns whether the method the {@link Parameters} was created for
     * contains a {@link Pageable} argument. This does not include a check if an
     * actual parameter was provided, so {@link #getPageable()} might return
     * {@literal null} even in case this method returns {@literal true}.
     * 
     * @return
     */
    public boolean hasPageableParameter() {

        return pageableIndex != -1;
    }


    /**
     * Returns the index of the {@link Pageable} {@link Method} parameter if
     * available. Will return {@literal -1} if there is no {@link Pageable}
     * argument in the {@link Method}'s parameter list.
     * 
     * @return the pageableIndex
     */
    public int getPageableIndex() {

        return pageableIndex;
    }


    /**
     * Returns whether the method the {@link Parameters} was created for
     * contains a {@link Sort} argument. This does not include a check if an
     * actual parameter was provided, so {@link #getSort()} might return
     * {@literal null} even in case this method returns {@literal true}.
     * 
     * @return
     */
    public boolean hasSortParameter() {

        return sortIndex != -1;
    }


    /**
     * Returns whether the parameter with the given index is a special
     * parameter.
     * 
     * @see #TYPES
     * @param index
     * @return
     */
    public final boolean isSpecialParameter(int index) {

        return TYPES.contains(method.getParameterTypes()[index]);
    }


    /**
     * Returns whether the parameter with the given index is annotated with
     * {@link Param}.
     * 
     * @param index
     * @return
     */
    public boolean isNamedParameter(int index) {

        return null != getParameterName(index);
    }


    /**
     * Returns the number of parameters.
     * 
     * @return
     */
    public int getNumberOfParameters() {

        return method.getParameterTypes().length;
    }


    /**
     * Asserts that either all of the non special parameters ({@link Pageable},
     * {@link Sort}) are annotated with {@link Param} or none of them is.
     * 
     * @param method
     */
    private void assertEitherAllParamAnnotatedOrNone() {

        boolean annotationFound = false;

        for (int index = 0; index < method.getParameterTypes().length; index++) {

            if (isSpecialParameter(index)) {
                Assert.isTrue(!isNamedParameter(index), PARAM_ON_SPECIAL);
                continue;
            }

            if (isNamedParameter(index)) {
                Assert.isTrue(annotationFound || index == 0, ALL_OR_NOTHING);
                annotationFound = true;
            } else {
                Assert.isTrue(!annotationFound, ALL_OR_NOTHING);
            }
        }
    }


    /**
     * Returns whether the given type is a bindable parameter.
     * 
     * @param type
     * @return
     */
    public static boolean isBindable(Class<?> type) {

        return !TYPES.contains(type);
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Class<?>> iterator() {

        return Arrays.asList(method.getParameterTypes()).iterator();
    }
}