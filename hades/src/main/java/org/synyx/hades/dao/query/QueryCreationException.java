package org.synyx.hades.dao.query;

/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class QueryCreationException extends RuntimeException {

    private static final long serialVersionUID = -1238456123580L;

    private static final String MESSAGE_TEMPLATE =
            "Could not create query for method %s! Could not find property %s on domain class %s.";


    public QueryCreationException(QueryMethod method, String propertyName) {

        super(String.format(MESSAGE_TEMPLATE, method, propertyName, method
                .getDomainClassName()));
    }
}
