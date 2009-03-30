/*
 * Copyright 2002-2008 the original author or authors.
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

package org.synyx.hades.dao.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.synyx.hades.dao.query.QueryLookupStrategy;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Class defining access to the DAO configuration abstracting the content of the
 * {@code dao-config} element in XML namespcae configuration. Defines default
 * values to populate resulting DAOs with.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class DaoConfigContext {

    protected static final String DEFAULT_DAO_BEAN_POSTFIX = "Dao";
    protected static final String DEFAULT_DAO_INTERFACE_POSTFIX = "Dao";
    protected static final String DEFAULT_DAO_IMPL_POSTFIX = "DaoImpl";

    protected static final String FINDER_LOOKUP_STRATEGY =
            "finder-lookup-strategy";
    protected static final String DAO_PACKAGE_NAME = "base-package";
    protected static final String DAO_NAME_POSTFIX = "dao-name-postfix";
    protected static final String DAO_IMPL_POSTFIX = "dao-impl-postfix";
    protected static final String DAO_INTERFACE_POSTFIX =
            "dao-interface-postfix";
    protected static final String DAO_BASE_CLASS_NAME = "dao-base-class";
    protected static final String FINDER_PREFIX = "finder-prefix";

    protected Element element;

    private Set<DaoContext> daoContexts;


    /**
     * Creates an instance of {@code XmlDaoConfigContext}.
     * 
     * @param daoConfigElement
     */
    public DaoConfigContext(final Element daoConfigElement) {

        Assert.notNull(daoConfigElement, "Element must not be null!");

        this.element = daoConfigElement;

        daoContexts = new HashSet<DaoContext>();
        NodeList nodes = daoConfigElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {

            Node node = nodes.item(i);

            boolean isElement = Node.ELEMENT_NODE == node.getNodeType();
            boolean isDao = "dao".equals(node.getLocalName());

            if (isElement && isDao) {

                DaoContext daoContext = new DaoContext((Element) node, this);
                daoContext.validate();

                daoContexts.add(daoContext);
            }
        }
    }


    /**
     * Validates the context. Ensures all required values set.
     */
    public void validate() {

    }


    /**
     * Returns all DAO contexts representing the {@code dao} elements declared
     * under {@code dao-config}.
     * 
     * @return
     */
    public Set<DaoContext> getDaoContexts() {

        return daoContexts;
    }


    /**
     * Returns the DOM element that holds the configuration.
     * 
     * @return
     */
    public Element getElement() {

        return element;
    }


    /**
     * Returns if the bean instances are configured manually.
     * 
     * @return
     */
    public boolean configureManually() {

        return daoContexts.size() > 0;
    }


    /**
     * Returns the strategy finder methods should be resolved.
     * 
     * @return
     */
    public QueryLookupStrategy getFinderLookupStrategy() {

        String createFinderQueries =
                element.getAttribute(FINDER_LOOKUP_STRATEGY);

        return StringUtils.hasText(createFinderQueries) ? QueryLookupStrategy
                .fromXml(createFinderQueries) : null;
    }


    /**
     * Returns the method name prefix that triggers finder resolution and
     * execution.
     * 
     * @return the finder prefix if one configured or {@code null} if not.
     */
    public String getFinderPrefix() {

        String finderPrefix = element.getAttribute(FINDER_PREFIX);

        return StringUtils.hasText(finderPrefix) ? finderPrefix : null;
    }


    /**
     * Returns the DAO package name.
     * 
     * @return the daoPackageName
     */
    protected String getDaoBasePackageName() {

        return element.getAttribute(DAO_PACKAGE_NAME);
    }


    /**
     * Returns the DAO interface postfix. This postfix is used to construct the
     * DAO interface name from the {@code dao} configuration elements. Defaults
     * to {@value #DEFAULT_DAO_BEAN_POSTFIX}.
     * 
     * @return the daoClassPostfix
     */
    protected String getDaoInterfacePostfix() {

        String daoInterfacePostfix =
                element.getAttribute(DAO_INTERFACE_POSTFIX);

        return StringUtils.hasText(daoInterfacePostfix) ? daoInterfacePostfix
                : DEFAULT_DAO_INTERFACE_POSTFIX;
    }


    /**
     * Returns the DAO bean name postfix. Defaults to
     * {@value #DEFAULT_DAO_BEAN_POSTFIX}.
     * 
     * @return the daoNamePostfix
     */
    protected String getDaoNamePostfix() {

        String postfix = element.getAttribute(DAO_NAME_POSTFIX);

        return StringUtils.hasText(postfix) ? postfix
                : DEFAULT_DAO_BEAN_POSTFIX;
    }


    /**
     * Returns the postfix used to lookup custom DAO implementations. Defaults
     * to {@value #DEFAULT_DAO_IMPL_POSTFIX}.
     * 
     * @return
     */
    protected String getDaoImplPostfix() {

        String postfix = element.getAttribute(DAO_IMPL_POSTFIX);
        return StringUtils.hasText(postfix) ? postfix
                : DEFAULT_DAO_IMPL_POSTFIX;
    }
}
