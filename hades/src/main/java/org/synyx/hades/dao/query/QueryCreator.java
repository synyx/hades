package org.synyx.hades.dao.query;

import static org.synyx.hades.dao.query.QueryUtils.*;

import java.util.Arrays;
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

                Part part = new Part(andPart, method, numberOfBlocks);

                andBuilder.append(part.getQueryPart()).append(" and ");
                numberOfBlocks += part.getNumberOfArguments();
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

    /**
     * A single part of a method name that has to be transformed into a query
     * part. The actual transformation is defined by a {@link Type} that is
     * determined from inspecting the given part. The query part can then be
     * looked up via {@link #getQueryPart()}.
     * 
     * @author Oliver Gierke
     */
    private static class Part {

        private final String part;

        private final Type type;
        private final int startIndex;
        private final QueryMethod method;


        /**
         * Creates a new {@link Part} from the given method name part, the
         * {@link QueryMethod} the part originates from and the start parameter
         * index.
         * 
         * @param part
         * @param method
         * @param startIndex
         */
        public Part(String part, QueryMethod method, int startIndex) {

            this.part = part;
            this.type = Type.fromProperty(part, method);
            this.startIndex = startIndex;
            this.method = method;
        }


        /**
         * Returns how many method parameters are bound by this part.
         * 
         * @return
         */
        public int getNumberOfArguments() {

            return type.getNumberOfArguments();
        }


        /**
         * Returns the query part.
         * 
         * @return
         */
        public String getQueryPart() {

            String property = type.extractProperty(part);

            if (!method.isValidField(property)) {
                throw QueryCreationException.invalidProperty(method, property);
            }

            return type.createQueryPart(StringUtils.uncapitalize(property),
                    method.getParameters(), startIndex);
        }

        /**
         * The type of a method name part. Used to create query parts in various
         * ways.
         * 
         * @author Oliver Gierke
         */
        private static enum Type {

            /**
             * Property to be bound to a {@code between} statement.
             */
            BETWEEN {

                /**
                 * Supports raw properties that end on 'between' and do not
                 * reference a field.
                 */
                @Override
                public boolean supports(String property, QueryMethod method) {

                    if (method.isValidField(property)) {
                        return false;
                    }

                    return property.endsWith("Between");
                }


                /**
                 * Binds 2 parameters.
                 */
                @Override
                public int getNumberOfArguments() {

                    return 2;
                }


                /**
                 * Removes training 'Between'.
                 */
                @Override
                public String extractProperty(String property) {

                    return property.substring(0, property.indexOf("Between"));
                }


                /**
                 * Creates part in the form of {@code x.$ property} between ?
                 * and ?} or the named equivalent.
                 */
                @Override
                public String createQueryPart(String property,
                        Parameters parameters, int index) {

                    String first = parameters.getPlaceholder(index);
                    String second = parameters.getPlaceholder(index + 1);

                    return String.format("x.%s between %s and %s", property,
                            first, second);
                }

            },

            /**
             * Simple plain property.
             */
            SIMPLE_PROPERTY {

                @Override
                public String createQueryPart(String property,
                        Parameters parameters, int index) {

                    return String.format("x.%s = %s", property, parameters
                            .getPlaceholder(index));
                }
            };

            /**
             * Returns the {@link Type} of the {@link Part} for the given raw
             * property and the given {@link QueryMethod}. This will try to
             * detect e.g. keywords contained in the raw property that trigger
             * special query creation. Returns {@link #SIMPLE_PROPERTY} by
             * default.
             * 
             * @param rawProperty
             * @param method
             * @return
             */
            public static Type fromProperty(String rawProperty,
                    QueryMethod method) {

                for (Type type : Arrays.asList(BETWEEN, SIMPLE_PROPERTY)) {
                    if (type.supports(rawProperty, method)) {
                        return type;
                    }
                }

                return SIMPLE_PROPERTY;
            }


            /**
             * Create the actual query part for the given property.
             * 
             * @param property the actual clean property
             * @param parameters
             * @param index
             * @return
             */
            public abstract String createQueryPart(String property,
                    Parameters parameters, int index);


            /**
             * Returns whether the the type supports the given raw property.
             * Implementations can detect keywords here or abstain from special
             * handling.
             * 
             * @param property
             * @param method
             * @return
             */
            protected boolean supports(String property, QueryMethod method) {

                return true;
            }


            /**
             * Returns the number of arguments the property binds. By default
             * this exactly one argument.
             * 
             * @return
             */
            public int getNumberOfArguments() {

                return 1;
            }


            /**
             * Callback method to extract the actual property to be bound from
             * the given part. Returns the raw part as is by default.
             * 
             * @param part
             * @return
             */
            public String extractProperty(String part) {

                return part;
            }
        }
    }
}
