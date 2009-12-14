package org.synyx.hades.dao.query;

import javax.persistence.Query;

import org.springframework.util.Assert;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;


/**
 * {@link ParameterBinder} is used to bind method parameters to a {@link Query}.
 * This is usually done whenever a {@link HadesQuery} is executed.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
class ParameterBinder {

    private final Parameters parameters;
    private final Object[] values;


    /**
     * Creates a new {@link ParameterBinder}.
     * 
     * @param parameters
     * @param values
     */
    public ParameterBinder(Parameters parameters, Object... values) {

        Assert.notNull(parameters);
        Assert.notNull(values);

        Assert.isTrue(parameters.getNumberOfParameters() == values.length,
                "Invalid number of parameters given!");

        this.parameters = parameters;
        this.values = values;
    }


    /**
     * Returns the {@link Pageable} of the parameters, if available. Returns
     * {@code null} otherwise.
     * 
     * @return
     */
    public Pageable getPageable() {

        if (!parameters.hasPageableParameter()) {
            return null;
        }

        return (Pageable) values[parameters.getPageableIndex()];
    }


    /**
     * Returns the sort instance to be used for query creation. Will use a
     * {@link Sort} parameter if available or the {@link Sort} contained in a
     * {@link Pageable} if available. Returns {@code null} if no {@link Sort}
     * can be found.
     * 
     * @return
     */
    public Sort getSort() {

        if (parameters.hasSortParameter()) {
            return (Sort) values[parameters.getPageableIndex()];
        }

        if (parameters.hasPageableParameter() && getPageable() != null) {
            return getPageable().getSort();
        }

        return null;
    }


    /**
     * Binds the parameters to the given {@link Query}.
     * 
     * @param query
     * @return
     */
    public Query bind(Query query) {

        int methodParameterPosition = 0;
        int queryParameterPosition = 1;

        for (Class<?> parameterType : parameters) {

            if (Parameters.isBindable(parameterType)) {

                Object parameter = values[methodParameterPosition];

                if (parameters.isNamedParameter(methodParameterPosition)) {
                    query.setParameter(parameters
                            .getParameterName(methodParameterPosition),
                            parameter);
                } else {
                    query.setParameter(queryParameterPosition++, parameter);
                }
            }

            methodParameterPosition++;
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

        if (!parameters.hasPageableParameter() || getPageable() == null) {
            return result;
        }

        result.setFirstResult(getPageable().getFirstItem());
        result.setMaxResults(getPageable().getPageSize());

        return result;
    }
}
