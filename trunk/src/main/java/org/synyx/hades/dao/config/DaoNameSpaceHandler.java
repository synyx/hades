package org.synyx.hades.dao.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * Simple namespace handler for {@literal dao-config} namespace.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke - gierke@synyx.de
 */
public class DaoNameSpaceHandler extends NamespaceHandlerSupport {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    public void init() {

        registerBeanDefinitionParser("dao-config",
                new DaoConfigDefinitionParser());
    }
}
