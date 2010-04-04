/*
 * Copyright 2008-2010 the original author or authors.
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
import java.util.ArrayList;
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
class Parameters implements Iterable<Parameter> {

    @SuppressWarnings("unchecked")
    static final List<Class<?>> TYPES =
            Arrays.asList(Pageable.class, Sort.class);

    private static final String ALL_OR_NOTHING =
            String
                    .format(
                            "Either use @%s "
                                    + "on all parameters except %s and %s typed once, or none at all!",
                            Param.class.getSimpleName(), Pageable.class
                                    .getSimpleName(), Sort.class
                                    .getSimpleName());

    private final int pageableIndex;
    private final int sortIndex;

    private final List<Parameter> parameters;


    /**
     * Creates a new instance of {@link Parameters}.
     * 
     * @param method
     */
    public Parameters(Method method) {

        Assert.notNull(method);

        this.parameters = new ArrayList<Parameter>();

        List<Class<?>> types = Arrays.asList(method.getParameterTypes());
        Annotation[][] annotations = method.getParameterAnnotations();

        for (int i = 0; i < types.size(); i++) {
            parameters
                    .add(new Parameter(types.get(i), annotations[i], this, i));
        }

        this.pageableIndex = types.indexOf(Pageable.class);
        this.sortIndex = types.indexOf(Sort.class);

        assertEitherAllParamAnnotatedOrNone();
    }


    /**
     * Creates a new {@link Parameters} instance with the given
     * {@link Parameter}s put into new context.
     * 
     * @param originals
     */
    private Parameters(List<Parameter> originals) {

        this.parameters = new ArrayList<Parameter>();

        int pageableIndex = -1;
        int sortIndex = -1;

        for (int i = 0; i < originals.size(); i++) {

            Parameter original = originals.get(i);

            this.parameters.add(new Parameter(original, this, i));

            pageableIndex = original.isPageable() ? i : -1;
            sortIndex = original.isSort() ? i : -1;
        }

        this.pageableIndex = pageableIndex;
        this.sortIndex = sortIndex;
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


    public Parameter getParameter(int index) {

        try {
            return parameters.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new ParameterOutOfBoundsException(e);
        }
    }


    /**
     * Returns whether the method signature contains one of the special
     * parameters ({@link Pageable}, {@link Sort}).
     * 
     * @return
     */
    public boolean hasSpecialParameter() {

        return hasSortParameter() || hasPageableParameter();
    }


    /**
     * Returns the number of parameters.
     * 
     * @return
     */
    public int getNumberOfParameters() {

        return parameters.size();
    }


    /**
     * Returns a {@link Parameters} instance with effectively all special
     * parameters removed.
     * 
     * @see Parameter#TYPES
     * @see Parameter#isSpecialParameter()
     * @return
     */
    public Parameters getBindableParameters() {

        List<Parameter> bindables = new ArrayList<Parameter>();

        for (Parameter candidate : this) {

            if (candidate.isBindable()) {
                bindables.add(candidate);
            }
        }

        return new Parameters(bindables);
    }


    /**
     * Returns the index of the placeholder inside a query for the parameter
     * with the given index. They might differ from the parameter index as the
     * method signature can contain special parameters (e.g. {@link Sort},
     * {@link Pageable}) that are not bound as plain query parameters but rather
     * handled differently.
     * 
     * @param index
     * @return the placeholder postion for the parameter with the given index.
     *         Will return 0 for special parameters.
     */
    int getPlaceholderPosition(Parameter parameter) {

        return parameter.isSpecialParameter() ? 0
                : getPlaceholderPositionRecursively(parameter);
    }


    private int getPlaceholderPositionRecursively(Parameter parameter) {

        int result = parameter.isSpecialParameter() ? 0 : 1;

        return parameter.isFirst() ? result : result
                + getPlaceholderPositionRecursively(parameter.getPrevious());
    }


    /**
     * Asserts that either all of the non special parameters ({@link Pageable},
     * {@link Sort}) are annotated with {@link Param} or none of them is.
     * 
     * @param method
     */
    private void assertEitherAllParamAnnotatedOrNone() {

        boolean annotationFound = false;

        for (Parameter parameter : this.getBindableParameters()) {

            if (parameter.isNamedParameter()) {
                Assert.isTrue(annotationFound || parameter.isFirst(),
                        ALL_OR_NOTHING);
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
    public Iterator<Parameter> iterator() {

        return parameters.iterator();
    }
}