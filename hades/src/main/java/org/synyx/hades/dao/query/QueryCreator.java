/*
 * Copyright 2008-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.synyx.hades.dao.query;

import static org.synyx.hades.dao.query.QueryUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.synyx.hades.domain.Order;
import org.synyx.hades.domain.Sort.Property;


/**
 * Class to encapsulate query creation logic for {@link QueryMethod}s.
 * 
 * @author Oliver Gierke
 */
class QueryCreator {

    private static final Logger LOG = LoggerFactory
            .getLogger(QueryCreator.class);
    private static final String INVALID_PARAMETER_SIZE =
            "You have to provide method arguments for each query "
                    + "criteria to construct the query correctly!";

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

        Assert.isTrue(!finderMethod.isModifyingQuery());

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
                new StringBuilder(getQueryString(READ_ALL_QUERY,
                        method.getDomainClassName()));
        queryBuilder.append(" where ");

        PartSource source = new PartSource(method.getName());

        // Split OR
        List<PartSource> orParts = source.getParts(OR);

        int parametersBound = 0;

        for (PartSource orPart : orParts) {

            // Split AND
            List<PartSource> andParts = orPart.getParts(AND);
            StringBuilder andBuilder = new StringBuilder();

            for (PartSource andPart : andParts) {

                Parameters parameters =
                        method.getParameters().getBindableParameters();

                Parameter parameter =
                        parameters.hasParameterAt(parametersBound) ? parameters
                                .getParameter(parametersBound) : null;

                try {
                    Part part =
                            new Part(andPart.cleanedUp(), method, parameter);

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

        if (source.hasOrderByClause()) {
            queryBuilder.append(" ").append(
                    source.getOrderBySource().getClause());
        }

        String query = queryBuilder.toString();

        LOG.debug("Created query '{}' from method {}", query, method.getName());

        return query;
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
            BETWEEN(null, "Between") {

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

            IS_NOT_NULL(null, "IsNotNull", "NotNull") {

                @Override
                public int getNumberOfArguments() {

                    return 0;
                }


                @Override
                public String createQueryPart(String property,
                        Parameter parameter) {

                    return String.format("x.%s is not null", property);
                }
            },

            IS_NULL(null, "IsNull", "Null") {

                @Override
                public int getNumberOfArguments() {

                    return 0;
                }


                @Override
                public String createQueryPart(String property,
                        Parameter parameter) {

                    return String.format("x.%s is null", property);
                }
            },

            LESS_THAN("<", "LessThan"), GREATER_THAN(">", "GreaterThan"), NOT_LIKE(
                    "not like", "NotLike"), LIKE("like", "Like"), NEGATING_SIMPLE_PROPERTY(
                    "<>", "Not"), SIMPLE_PROPERTY("=");

            // Need to list them again explicitly as the order is important
            // (esp. for IS_NULL, IS_NOT_NULL)
            private static final List<Type> ALL = Arrays.asList(IS_NOT_NULL,
                    IS_NULL, BETWEEN, LESS_THAN, GREATER_THAN, NOT_LIKE, LIKE,
                    NEGATING_SIMPLE_PROPERTY, SIMPLE_PROPERTY);
            private List<String> keywords;
            private String operator;


            /**
             * Creates a new {@link Type} using the given keyword and operator.
             * Both can be {@literal null}.
             * 
             * @param operator
             * @param keywords
             */
            private Type(String operator, String... keywords) {

                this.keywords = Arrays.asList(keywords);
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

                for (Type type : ALL) {
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

                if (keywords == null) {
                    return true;
                }

                if (method.isValidField(property)) {
                    return false;
                }

                for (String keyword : keywords) {
                    if (property.endsWith(keyword)) {
                        return true;
                    }
                }

                return false;
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

                for (String keyword : keywords) {
                    if (part.endsWith(keyword)) {
                        return part.substring(0, part.indexOf(keyword));
                    }
                }

                return part;
            }
        }
    }

    /**
     * Helper class to split a method name into all of its logical parts
     * (prefix, properties, postfix).
     * 
     * @author Oliver Gierke
     */
    private static class PartSource {

        private static final String ORDER_BY = "OrderBy";
        private static final String[] PREFIXES = new String[] { "findBy",
                "find", "readBy", "read", "getBy", "get" };

        private final String cleanedUpString;
        private final OrderBySource orderBySource;


        public PartSource(String methodName) {

            String removedPrefixes = strip(methodName);

            String[] parts = split(removedPrefixes, ORDER_BY);

            if (parts.length > 2) {
                throw new IllegalArgumentException(
                        "OrderBy must not be used more than once in a method name!");
            }

            this.cleanedUpString = parts[0];
            this.orderBySource =
                    parts.length == 2 ? new OrderBySource(parts[1]) : null;
        }


        public OrderBySource getOrderBySource() {

            return orderBySource;
        }


        public boolean hasOrderByClause() {

            return orderBySource != null;
        }


        public List<PartSource> getParts(String keyword) {

            List<PartSource> parts = new ArrayList<PartSource>();
            for (String part : split(cleanedUpString, keyword)) {
                parts.add(new PartSource(part));
            }

            return parts;
        }


        public String cleanedUp() {

            return cleanedUpString;
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
         * Splits the given text at the given keywords. Expects camelcase style
         * to only match concrete keywords and not derivatives of it.
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
    }

    /**
     * Simple helper class to create a JPA order by clause from a method name
     * end. It expects the last part of the method name to be given and supports
     * lining up multiple properties ending with the sorting direction. So the
     * following method end would be valid: {@code LastnameUsernameDesc}. This
     * would create a clause {@code order by x.lastname, x.username desc}.
     * 
     * @author Oliver Gierke
     */
    static class OrderBySource {

        private final String BLOCK_SPLIT = "(?<=Asc|Desc)(?=[A-Z])";
        private final Pattern DIRECTION_SPLIT = Pattern
                .compile("(.+)(Asc|Desc)$");

        private final List<Property> orders;


        public OrderBySource(String clause) {

            this.orders = new ArrayList<Property>();

            for (String part : clause.split(BLOCK_SPLIT)) {

                Matcher matcher = DIRECTION_SPLIT.matcher(part);

                if (!matcher.find()) {
                    throw new IllegalArgumentException(String.format(
                            "Invalid order syntax for part %s!", part));
                }

                Order direction = Order.fromJpaValue(matcher.group(2));
                String property = StringUtils.uncapitalize(matcher.group(1));

                this.orders.add(new Property(direction, property));
            }
        }


        /**
         * Returns the final JPA order by clause.
         * 
         * @return
         */
        public String getClause() {

            StringBuilder builder = new StringBuilder("order by ");

            for (Property property : this.orders) {

                builder.append("x.").append(property.getName());
                builder.append(" ").append(property.getOrder().getJpaValue());
                builder.append(", ");
            }

            builder.delete(builder.length() - 2, builder.length());
            return builder.toString();
        }
    }
}
