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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.synyx.hades.dao.Param;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;


/**
 * Class to abstract a single parameter of a query method. It is held in the
 * context of a {@link Parameters} instance.
 * 
 * @author Oliver Gierke
 */
class Parameter {

    @SuppressWarnings("unchecked")
    static final List<Class<?>> TYPES =
            Arrays.asList(Pageable.class, Sort.class);

    private static final String PARAM_ON_SPECIAL =
            String.format(
                    "You must not user @%s on a parameter typed %s or %s",
                    Param.class.getSimpleName(),
                    Pageable.class.getSimpleName(), Sort.class.getSimpleName());

    private static final String NAMED_PARAMETER_TEMPLATE = ":%s";
    private static final String POSITION_PARAMETER_TEMPLATE = "?%s";

    private final Class<?> type;
    private final Set<Annotation> annotations;
    private final Parameters parameters;
    private final int index;


    /**
     * Creates a new {@link Parameter} for the given type, {@link Annotation}s,
     * positioned at the given index inside the given {@link Parameters}.
     * 
     * @param type
     * @param annotations
     * @param parameters
     * @param index
     */
    public Parameter(Class<?> type, Annotation[] annotations,
            Parameters parameters, int index) {

        this(type, new HashSet<Annotation>(), parameters, index);

        for (Annotation annotation : annotations) {
            this.annotations.add(annotation);
        }

        if (isSpecialParameter() && isNamedParameter()) {
            throw new IllegalArgumentException(PARAM_ON_SPECIAL);
        }
    }


    /**
     * Creates a new {@link Parameter} for the given type, {@link Annotation}s,
     * positioned at the given index inside the given {@link Parameters}.
     * 
     * @param type
     * @param annotations
     * @param parameters
     * @param index
     */
    Parameter(Class<?> type, Set<Annotation> annotations,
            Parameters parameters, int index) {

        Assert.notNull(type);
        Assert.notNull(annotations);
        Assert.notNull(parameters);

        this.parameters = parameters;
        this.index = index;

        this.type = type;
        this.annotations = annotations;
    }


    /**
     * Copy constructor to put a {@link Parameter} into another context.
     * 
     * @param parameter
     * @param parameters
     * @param index
     */
    Parameter(Parameter parameter, Parameters parameters, int index) {

        this(parameter.type, parameter.annotations, parameters, index);
    }


    /**
     * Returns whether the {@link Parameter} is the first one.
     * 
     * @return
     */
    boolean isFirst() {

        return index == 0;
    }


    /**
     * Returns the next {@link Parameter} from the surrounding
     * {@link Parameters}.
     * 
     * @throws ParameterOutOfBoundsException
     * @return
     */
    public Parameter getNext() {

        return parameters.getParameter(index + 1);
    }


    /**
     * Returns the previous {@link Parameter}.
     * 
     * @return
     */
    Parameter getPrevious() {

        return parameters.getParameter(index - 1);
    }


    /**
     * Returns whether the parameter is a special parameter.
     * 
     * @see #TYPES
     * @param index
     * @return
     */
    public boolean isSpecialParameter() {

        return TYPES.contains(type);
    }


    /**
     * Returns whether the {@link Parameter} is to be bound to a query.
     * 
     * @return
     */
    public boolean isBindable() {

        return !isSpecialParameter();
    }


    /**
     * Returns the placeholder to be used for the parameter. Can either be a
     * named one or positional.
     * 
     * @param index
     * @return
     */
    public String getPlaceholder() {

        if (isNamedParameter()) {
            return String.format(NAMED_PARAMETER_TEMPLATE, getParameterName());
        } else {
            return String.format(POSITION_PARAMETER_TEMPLATE,
                    getParameterPosition());
        }
    }


    /**
     * Returns the position index the parameter is bound to in the context of
     * its surrounding {@link Parameters}.
     * 
     * @return
     */
    public int getParameterPosition() {

        return parameters.getPlaceholderPosition(this);
    }


    /**
     * Returns whether the parameter is annotated with {@link Param}.
     * 
     * @param index
     * @return
     */
    public boolean isNamedParameter() {

        return getParameterName() != null;
    }


    /**
     * Returns the name of the parameter (through {@link Param} annotation) or
     * null if none can be found.
     * 
     * @return
     */
    public String getParameterName() {

        for (Annotation annotation : annotations) {
            if (annotation instanceof Param) {
                return ((Param) annotation).value();
            }
        }

        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder(type.getName());
        builder.append(StringUtils
                .collectionToDelimitedString(annotations, " "));

        return builder.toString();
    }


    /**
     * Returns whether the parameter is a {@link Pageable} parameter.
     * 
     * @return
     */
    public boolean isPageable() {

        return Pageable.class.isAssignableFrom(type);
    }


    /**
     * @return
     */
    public boolean isSort() {

        return Sort.class.isAssignableFrom(type);
    }
}
