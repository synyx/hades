/*
 * Copyright 2002-2008 the original author or authors.
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

package org.synyx.hades.dao.config;

import org.springframework.util.StringUtils;
import org.synyx.hades.dao.orm.QueryLookupStrategy;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * XML based implementation of {@link DaoConfigContext}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
class XmlDaoConfigContext extends DaoConfigContext {

    private Element daoConfigElement;


    /**
     * Creates an instance of {@code XmlDaoConfigContext}.
     * 
     * @param daoConfigElement
     */
    public XmlDaoConfigContext(final Element daoConfigElement) {

        this.daoConfigElement = daoConfigElement;
    }


    /**
     * Returns the root configuration node.
     * 
     * @return
     */
    public Node getRootNode() {

        return daoConfigElement;
    }


    /**
     * Returns the child nodes of the root configuration node.
     * 
     * @return
     */
    public NodeList getChildNodes() {

        return daoConfigElement.getChildNodes();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#configureManually()
     */
    @Override
    public boolean configureManually() {

        if (!daoConfigElement.hasChildNodes()) {
            return false;
        }

        NodeList nodes = getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {

            Node node = nodes.item(i);

            // Found an XML element?
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                return true;
            }
        }

        return false;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoPackageName()
     */
    @Override
    protected String getDaoPackageName() {

        return daoConfigElement.getAttribute("dao-package-name");
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getEntityPackageName()
     */
    @Override
    protected String getEntityPackageName() {

        return daoConfigElement.getAttribute("entity-package-name");
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoClassPostfix()
     */
    @Override
    protected String getDaoInterfacePostfix() {

        String daoInterfacePostfix = daoConfigElement
                .getAttribute("dao-interface-postfix");

        return StringUtils.hasText(daoInterfacePostfix) ? daoInterfacePostfix
                : DEFAULT_DAO_INTERFACE_POSTFIX;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoNamePostfix()
     */
    @Override
    public String getDaoNamePostfix() {

        String postfix = daoConfigElement.getAttribute("dao-name-postfix");

        return StringUtils.hasText(postfix) ? postfix
                : DEFAULT_DAO_BEAN_POSTFIX;
    }


    @Override
    protected String getDaoImplPostfix() {

        String postfix = daoConfigElement.getAttribute("dao-impl-postfix");
        return StringUtils.hasText(postfix) ? postfix
                : DEFAULT_DAO_IMPL_POSTFIX;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoBaseClassName()
     */
    @Override
    protected String getDaoBaseClassName() {

        return daoConfigElement.getAttribute("dao-base-class");
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#createFinderQueries()
     */
    @Override
    public QueryLookupStrategy getFinderLookupStrategy() {

        String createFinderQueries = daoConfigElement
                .getAttribute("create-finder-queries");

        return StringUtils.hasText(createFinderQueries) ? QueryLookupStrategy
                .fromXml(createFinderQueries) : DEFAULT_CREATE_FINDER_QUERIES;
    }
}