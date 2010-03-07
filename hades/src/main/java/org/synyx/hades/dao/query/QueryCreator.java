package org.synyx.hades.dao.query;

import static org.synyx.hades.dao.query.QueryUtils.*;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;


/**
 * Class to encapsulate query creation logic for {@link QueryMethod}s.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
class QueryCreator {

    private static final Log LOG = LogFactory.getLog(QueryCreator.class);
    private static final String[] PREFIXES =
            new String[] { "findBy", "find", "readBy", "read", "getBy", "get" };

    private static final String AND = "And";
    private static final String OR = "Or";

    private static final String AND_TEMPLATE = "x.%s = ? and ";
    private static final String NAMED_AND_TEMPLATE = "x.%s = :%s and ";
    private static final String KEYWORD_TEMPLATE = "(%s)(?=[A-Z])";
    private static final String PREFIX_TEMPLATE = "^%s(?=[A-Z]).*";

    private QueryMethod method;


    /**
     * Creates a new {@link QueryCreator} for the given {@link QueryMethod}.
     * 
     * @param finderMethod
     */
    public QueryCreator(QueryMethod finderMethod) {

        this.method = finderMethod;
    }


    /**
     * Constructs a query from the underlying {@link QueryMethod}.
     * 
     * @return the query string
     * @throws QueryCreationException
     */
    String constructQuery() {

        StringBuilder queryBuilder =
                new StringBuilder(getQueryString(READ_ALL_QUERY, method
                        .getDomainClassName()));
        queryBuilder.append(" where ");

        // Split OR
        String[] orParts = split(strip(method.getName()), OR);

        int numberOfBlocks = 0;

        for (String orPart : orParts) {

            // Split AND
            String[] andParts = split(orPart, AND);

            StringBuilder andBuilder = new StringBuilder();

            for (String andPart : andParts) {
                andBuilder.append(buildAndPart(andPart, numberOfBlocks));
                numberOfBlocks++;
            }

            andBuilder.delete(andBuilder.length() - 5, andBuilder.length());

            queryBuilder.append(andBuilder);
            queryBuilder.append(" or ");
        }

        // Assert correct number of parameters
        if (!method.isCorrectNumberOfParameters(numberOfBlocks)) {
            throw new IllegalArgumentException(
                    "You have to provide method arguments for each query "
                            + "criteria to construct the query correctly!");
        }

        queryBuilder.delete(queryBuilder.length() - 4, queryBuilder.length());

        String query = queryBuilder.toString();

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Created query '%s' from method %s", query,
                    method.getName()));
        }

        return query;
    }


    /**
     * Build the JPQL string to add a constraint on a property. It will create a
     * named parameter constraint if the method parameter is a named one, too.
     * 
     * @param property
     * @param index
     * @return
     */
    private String buildAndPart(String property, int index) {

        if (!method.isValidField(property)) {
            throw QueryCreationException.invalidProperty(method, property);
        }

        Parameters parameters = method.getParameters();
        String propertyName = StringUtils.uncapitalize(property);

        if (parameters.isNamedParameter(index)) {

            String parameterName = parameters.getParameterName(index);

            return String.format(NAMED_AND_TEMPLATE, propertyName,
                    parameterName);
        } else {

            return String.format(AND_TEMPLATE, StringUtils
                    .uncapitalize(property));
        }
    }


    /**
     * Splits the given text at the given keywords. Expects camelcase style to
     * only match concrete keywords and not derivatives of it.
     * 
     * @param text
     * @param keyword
     * @return
     */
    private String[] split(String text, String keyword) {

        String regex = String.format(KEYWORD_TEMPLATE, keyword);

        Pattern pattern = Pattern.compile(regex);
        return pattern.split(text);
    }


    /**
     * Strips a prefix from the given method name if it starts with one of
     * {@value #PREFIXES}.
     * 
     * @param methodName
     * @return
     */
    private String strip(String methodName) {

        for (String prefix : PREFIXES) {

            String regex = String.format(PREFIX_TEMPLATE, prefix);
            if (methodName.matches(regex)) {
                return methodName.substring(prefix.length());
            }
        }

        return methodName;
    }
}
