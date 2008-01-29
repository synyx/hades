package org.synyx.jpa.support.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * Simple namespace handler for <code>dao-config</code> namespace.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
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
