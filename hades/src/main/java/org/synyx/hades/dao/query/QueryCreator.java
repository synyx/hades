package org.synyx.hades.dao.query;

import static org.synyx.hades.dao.query.QueryUtils.*;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;


/**
 * Class to encapsulate query creation logic for {@link FinderMethod}s.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class QueryCreator {

    private static final Log LOG = LogFactory.getLog(QueryCreator.class);

    private static final String AND = "And";
    private static final String OR = "Or";

    private static final String AND_TEMPLATE = "x.%s = ? and ";
    private static final String REGEX_TEMPLATE = "(%s)(?=[A-Z])";

    private FinderMethod method;


    /**
     * Creates a new {@link QueryCreator} for the given {@link FinderMethod}.
     * 
     * @param finderMethod
     */
    public QueryCreator(FinderMethod finderMethod) {

        this.method = finderMethod;
    }


    /**
     * Constructs a query from the underlying {@link FinderMethod}.
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
        String[] orParts = split(method.getUnprefixedMethodName(), OR);

        int numberOfBlocks = 0;

        for (String orPart : orParts) {

            // Split AND
            String[] andParts = split(orPart, AND);

            StringBuilder andBuilder = new StringBuilder();

            for (String andPart : andParts) {

                if (!method.isValidField(andPart)) {
                    throw new QueryCreationException(method, andPart);
                }

                andBuilder.append(String.format(AND_TEMPLATE, StringUtils
                        .uncapitalize(andPart)));

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
     * Splits the given text at the given keywords. Expects camelcase style to
     * only match concrete keywords and not derivatives of it.
     * 
     * @param text
     * @param keyword
     * @return
     */
    private String[] split(String text, String keyword) {

        String regex = String.format(REGEX_TEMPLATE, keyword);

        Pattern pattern = Pattern.compile(regex);
        return pattern.split(text);
    }
}
