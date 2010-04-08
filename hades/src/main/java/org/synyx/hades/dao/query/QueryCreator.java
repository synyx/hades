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
    private static final String INVALID_PARAMETER_SIZE =
            "You have to provide method arguments for each query "
                    + "criteria to construct the query correctly!";
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

        int parametersBound = 0;

        for (String orPart : orParts) {

            // Split AND
            String[] andParts = split(orPart, AND);

            StringBuilder andBuilder = new StringBuilder();

            for (String andPart : andParts) {

                Parameters parameters =
                        method.getParameters().getBindableParameters();

                try {
                    Part part =
                            new Part(andPart, method, parameters
                                    .getParameter(parametersBound));

                    andBuilder.append(part.getQueryPart()).append(" and ");
                    parametersBound += part.getNumberOfArguments();

                } catch (ParameterOutOfBoundsException e) {
                    throw QueryCreationException.create(method, e);
                }

            }

            andBuilder.delete(andBuilder.length() - 5, andBuilder.length());

            queryBuilder.append(andBuilder);
            queryBuilder.append(" or ");
        }

        // Assert correct number of parameters
        if (!method.isCorrectNumberOfParameters(parametersBound)) {
            throw QueryCreationException.create(method, INVALID_PARAMETER_SIZE);
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
        private final Parameter parameter;
        private final QueryMethod method;


        /**
         * Creates a new {@link Part} from the given method name part, the
         * {@link QueryMethod} the part originates from and the start parameter
         * index.
         * 
         * @param part
         * @param method
         * @param parameter
         */
        public Part(String part, QueryMethod method, Parameter parameter) {

            this.part = part;
            this.type = Type.fromProperty(part, method);
            this.method = method;
            this.parameter = parameter;
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
                    parameter);
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
            BETWEEN("Between", null) {

                /**
                 * Binds 2 parameters.
                 */
                @Override
                public int getNumberOfArguments() {

                    return 2;
                }


                /**
                 * Creates part in the form of {@code x.$ property} between ?
                 * and ?} or the named equivalent.
                 */
                @Override
                public String createQueryPart(String property,
                        Parameter parameter) {

                    String first = parameter.getPlaceholder();
                    String second = parameter.getNext().getPlaceholder();

                    return String.format("x.%s between %s and %s", property,
                            first, second);
                }

            },

            LESS_THAN("LessThan", "<"), GREATER_THAN("GreaterThan", ">"), SIMPLE_PROPERTY(
                    null, "=");

            private String keyword;
            private String operator;


            /**
             * Creates a new {@link Type} using the given keyword and operator.
             * Both can be {@literal null}.
             * 
             * @param keyword
             * @param operator
             */
            private Type(String keyword, String operator) {

                this.keyword = keyword;
                this.operator = operator;
            }


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

                for (Type type : Arrays.asList(BETWEEN, LESS_THAN,
                        GREATER_THAN, SIMPLE_PROPERTY)) {
                    if (type.supports(rawProperty, method)) {
                        return type;
                    }
                }

                return SIMPLE_PROPERTY;
            }


            /**
             * Create the actual query part for the given property. Creates a
             * simple assignment of the following shape by default. {@code x.$
             * property} ${operator} ${parameterPlaceholder}}.
             * 
             * @param property the actual clean property
             * @param parameters
             * @param index
             * @return
             */
            public String createQueryPart(String property, Parameter parameter) {

                return String.format("x.%s %s %s", property, operator,
                        parameter.getPlaceholder());
            }


            /**
             * Returns whether the the type supports the given raw property.
             * Default implementation checks,. whether the property ends with
             * the registered keyword. Does not support the keyword if the
             * property is a valid field as is.
             * 
             * @param property
             * @param method
             * @return
             */
            protected boolean supports(String property, QueryMethod method) {

                if (keyword == null) {
                    return true;
                }

                if (method.isValidField(property)) {
                    return false;
                }

                return property.endsWith(keyword);
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
             * the given part. Strips the keyword from the part's end if
             * available.
             * 
             * @param part
             * @return
             */
            public String extractProperty(String part) {

                return keyword == null ? part : part.substring(0, part
                        .indexOf(keyword));
            }
        }
    }
}
