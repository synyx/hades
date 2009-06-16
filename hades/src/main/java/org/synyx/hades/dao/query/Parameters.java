package org.synyx.hades.dao.query;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.util.ClassUtils;


/**
 * Abstracts method parameters that have to be bound to query parameters or
 * applied to the query independently.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
class Parameters {

    @SuppressWarnings("unchecked")
    public static final List<Class<?>> TYPES =
            Arrays.asList(Pageable.class, Sort.class);

    private int pageableIndex;
    private int sortIndex;

    private Object[] parameters;


    /**
     * Creates a new instance of {@link Parameters}.
     * 
     * @param method
     * @param parameters
     */
    public Parameters(Method method, Object... parameters) {

        List<Class<?>> types = Arrays.asList(method.getParameterTypes());
        this.pageableIndex = types.indexOf(Pageable.class);
        this.sortIndex = types.indexOf(Sort.class);
        this.parameters = parameters;
    }


    /**
     * Binds the parameters to the given {@link Query}.
     * 
     * @param query
     * @return
     */
    public Query bind(Query query) {

        int position = 1;

        for (Object parameter : parameters) {
            if (!ClassUtils.isOfType(parameter, TYPES)) {
                query.setParameter(position++, parameter);
            }
        }

        return query;
    }


    /**
     * Binds the parameters to the given query and applies special parameter
     * types (e.g. pagination).
     * 
     * @param query
     * @return
     */
    public Query bindAndPrepare(Query query) {

        Query result = bind(query);

        if (!hasPageableParameter()) {
            return result;
        }

        result.setFirstResult(getPageable().getFirstItem());
        result.setMaxResults(getPageable().getPageSize());

        return result;
    }


    /**
     * Applies sorting to the given query string.
     * 
     * @param query
     * @return
     */
    public String applySorting(String query) {

        return QueryUtils.applySorting(query, getSort());
    }


    /**
     * Returns the {@link Pageable} of the parameters, if available. Returns
     * {@code null} otherwise.
     * 
     * @return
     */
    public Pageable getPageable() {

        if (!hasPageableParameter()) {
            return null;
        }

        return (Pageable) parameters[pageableIndex];
    }


    /**
     * Returns whether the parameters contain a {@link Pageable}.
     * 
     * @return
     */
    private boolean hasPageableParameter() {

        return pageableIndex != -1;
    }


    /**
     * Returns whether the parameters contain a {@link Sort}.
     * 
     * @return
     */
    private boolean hasSortParameter() {

        return sortIndex != -1;
    }


    /**
     * Returns the sort instance to be used for query creation. Will use a
     * {@link Sort} parameter if available or the {@link Sort} contained in a
     * {@link Pageable} if available. Returns {@code null} if no {@link Sort}
     * can be found.
     * 
     * @return
     */
    private Sort getSort() {

        if (hasSortParameter()) {
            return (Sort) parameters[sortIndex];
        }

        if (hasPageableParameter()) {
            return getPageable().getSort();
        }

        return null;
    }
}