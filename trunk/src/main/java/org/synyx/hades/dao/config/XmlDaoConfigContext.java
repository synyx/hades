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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * XML based implementation of {@link DaoConfigContext}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
class XmlDaoConfigContext implements DaoConfigContext {

    private static final String DEFAULT_DAO_POSTFIX = "Dao";

    private Element daoConfigElement;


    /**
     * Creates an instance of {@code XmlDaoConfigContext}.
     * 
     * @param daoConfigElement
     */
    public XmlDaoConfigContext(final Element daoConfigElement) {

        this.daoConfigElement = daoConfigElement;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getChildNodes()
     */
    public NodeList getChildNodes() {

        return daoConfigElement.getChildNodes();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#configureManually()
     */
    public boolean configureManually() {

        return daoConfigElement.hasChildNodes();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoPackageName()
     */
    public String getDaoPackageName() {

        return daoConfigElement.getAttribute("dao-package-name");
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getEntityPackageName()
     */
    public String getEntityPackageName() {

        return daoConfigElement.getAttribute("entity-package-name");
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoClassPostfix()
     */
    public String getDaoClassPostfix() {

        return daoConfigElement.getAttribute("dao-class-postfix");
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoNamePostfix()
     */
    public String getDaoNamePostfix() {

        String postfix = daoConfigElement.getAttribute("dao-name-postfix");

        return (!StringUtils.hasText(postfix)) ? DEFAULT_DAO_POSTFIX : postfix;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.config.DaoConfigContext#getDaoBaseClassName()
     */
    public String getDaoBaseClassName() {

        return daoConfigElement.getAttribute("dao-base-class");
    }
}