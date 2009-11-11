package org.synyx.hades.dao.query;

/**
 * Exception to be thrown if a query cannot be created from a
 * {@link QueryMethod}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class QueryCreationException extends RuntimeException {

    private static final long serialVersionUID = -1238456123580L;

    private static final String MESSAGE_TEMPLATE =
            "Could not create query for method %s! Could not find property %s on domain class %s.";


    /**
     * Creates a new {@link QueryCreationException}.
     * 
     * @param method
     */
    private QueryCreationException(String message) {

        super(message);
    }


    /**
     * Rejects the given domain class property.
     * 
     * @param method
     * @param propertyName
     * @return
     */
    public static QueryCreationException invalidProperty(QueryMethod method,
            String propertyName) {

        return new QueryCreationException(String.format(MESSAGE_TEMPLATE,
                method, propertyName, method.getDomainClassName()));
    }


    /**
     * Creates a new {@link QueryCreationException}.
     * 
     * @param method
     * @param message
     * @return
     */
    public static QueryCreationException create(QueryMethod method,
            String message) {

        return new QueryCreationException(String.format(
                "Could not create query for %s! Reason: %s", method, message));
    }
}
