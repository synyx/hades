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

import static org.springframework.core.annotation.AnnotationUtils.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.synyx.hades.dao.Modifying;
import org.synyx.hades.dao.QueryHints;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.util.ClassUtils;


/**
 * Abstraction of a method that is designated to execute a finder query.
 * Enriches the standard {@link Method} interface with Hades specific
 * information that is necessary to construct {@link HadesQuery}s for the
 * method.
 * 
 * @author Oliver Gierke
 */
public class QueryMethod {

    private final Method method;
    private final Parameters parameters;
    private final Class<?> domainClass;

    private final QueryExtractor extractor;


    /**
     * Creates a new {@link QueryMethod} from the given parameters. Looks up the
     * correct query to use for following invocations of the method given.
     * 
     * @param method
     * @param domainClass
     * @param extractor
     */
    public QueryMethod(Method method, Class<?> domainClass,
            QueryExtractor extractor) {

        Assert.notNull(method, "Method must not be null!");
        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(extractor, "Query extractor must not be null!");

        for (Class<?> type : Parameters.TYPES) {
            if (ClassUtils.getNumberOfOccurences(method, type) > 1) {
                throw new IllegalStateException(String.format(
                        "Method must only one argument of type %s!", type
                                .getSimpleName()));
            }
        }

        if (ClassUtils.hasParameterOfType(method, Pageable.class)) {
            ClassUtils.assertReturnType(method, Page.class, List.class);
            if (ClassUtils.hasParameterOfType(method, Sort.class)) {
                throw new IllegalStateException(
                        "Method must not have Pageable *and* Sort parameter. "
                                + "Use sorting capabilities on Pageble instead!");
            }
        }

        this.method = method;
        this.parameters = new Parameters(method);
        this.domainClass = domainClass;
        this.extractor = extractor;

        Assert.isTrue(
                !(isModifyingQuery() && parameters.hasSpecialParameter()),
                String.format("Modifying method must not contain %s!",
                        Parameters.TYPES));

        if (parameters.hasPageableParameter() && !extractor.canExtractQuery()) {
            throw new IllegalArgumentException(
                    "You cannot use Pageable as method parameter if your "
                            + "persistence provider cannot extract queries!");
        }
    }


    /**
     * Returns the method's name.
     * 
     * @return
     */
    String getName() {

        return method.getName();
    }


    /**
     * Returns whether the given
     * 
     * @param number
     * @return
     */
    boolean isCorrectNumberOfParameters(int number) {

        return number == parameters.getBindableParameters()
                .getNumberOfParameters();
    }


    /**
     * Returns whether the given field is valid field name and thus a persistent
     * field to the underlying domain class.
     * 
     * @param fieldName
     * @return
     */
    boolean isValidField(String fieldName) {

        Class<?> returnType = ClassUtils.getReturnedDomainClass(method);

        if (null != ReflectionUtils.findMethod(returnType, "get" + fieldName)) {
            return true;
        }

        return null != ReflectionUtils.findField(returnType, StringUtils
                .uncapitalize(fieldName));
    }


    /**
     * Returns the name of the domain class the finder belongs to.
     * 
     * @return
     */
    String getDomainClassName() {

        return ClassUtils.getReturnedDomainClass(method).getSimpleName();
    }


    /**
     * Returns the {@link org.synyx.hades.dao.Query} annotation that is applied
     * to the method or {@code null} if none available.
     * 
     * @return
     */
    org.synyx.hades.dao.Query getQueryAnnotation() {

        return method.getAnnotation(org.synyx.hades.dao.Query.class);
    }


    /**
     * Returns whether the finder will actually return a collection of entities
     * or a single one.
     * 
     * @return
     */
    boolean isCollectionQuery() {

        Class<?> returnType = method.getReturnType();
        return org.springframework.util.ClassUtils.isAssignable(List.class,
                returnType);
    }


    /**
     * Returns whether the finder will return a {@link Page} of results.
     * 
     * @return
     */
    boolean isPageQuery() {

        Class<?> returnType = method.getReturnType();
        return org.springframework.util.ClassUtils.isAssignable(Page.class,
                returnType);
    }


    /**
     * Returns whether the finder is a modifying one.
     * 
     * @return
     */
    boolean isModifyingQuery() {

        return null != AnnotationUtils.findAnnotation(method, Modifying.class);
    }


    /**
     * Returns the {@link Parameters} wrapper to gain additional information
     * about {@link Method} parameters.
     * 
     * @return
     */
    Parameters getParameters() {

        return parameters;
    }


    /**
     * Returns the name of the {@link javax.persistence.NamedQuery} this method
     * belongs to.
     * 
     * @return
     */
    String getNamedQueryName() {

        return domainClass.getSimpleName() + "." + method.getName();
    }


    /**
     * Returns the {@link QueryExtractor}.
     * 
     * @return
     */
    QueryExtractor getQueryExtractor() {

        return extractor;
    }


    /**
     * Returns all {@link QueryHint}s annotated at this class. Note, that
     * {@link QueryHints}
     * 
     * @return
     */
    List<QueryHint> getHints() {

        List<QueryHint> result = new ArrayList<QueryHint>();

        QueryHints hints = getAnnotation(method, QueryHints.class);
        if (hints != null) {
            result.addAll(Arrays.asList(hints.value()));
        }

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return method.toString();
    }
}
