package org.synyx.dao.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.synyx.dao.orm.support.GenericDaoFactoryBean;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Parser to create bean definitions for dao-config namespace. Registers bean
 * definitions for DAOs as well as
 * {@code PersistenceExceptionTranslationPostProcessor} and
 * {@code PersistenceExceptionTranslationPostProcessor} to transparently inject
 * entity manager factory instance and apply exception translation.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke - gierke@synyx.de
 */
public class DaoConfigDefinitionParser implements BeanDefinitionParser {

    private static final String DEFAULT_DAO_POSTFIX = "Dao";

    private static final Class<?> PAB_POST_PROCESSOR = PersistenceAnnotationBeanPostProcessor.class;
    private static final Class<?> PET_POST_PROCESSOR = PersistenceExceptionTranslationPostProcessor.class;


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element,
     *      org.springframework.beans.factory.xml.ParserContext)
     */
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        String daoPackageName = element.getAttribute("dao-package-name");
        String entityPackageName = element.getAttribute("entity-package-name");
        String daoClassPostfix = element.getAttribute("dao-class-postfix");
        String daoNamePostfix = element.getAttribute("dao-name-postfix");
        String daoBaseClassName = element.getAttribute("dao-base-class");

        // Set default postfix if none configured
        daoClassPostfix = (null == daoClassPostfix) ? DEFAULT_DAO_POSTFIX
                : daoClassPostfix;

        NodeList childNodes = element.getChildNodes();

        BeanDefinitionRegistry registry = parserContext.getRegistry();

        // Add dao declarations
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {

                Element childElement = (Element) childNode;

                String name = childElement.getAttribute("name");
                String entityClassName = name.substring(0, 1).toUpperCase()
                        + name.substring(1);

                String fullQualifiedEntityClassName = entityPackageName + "."
                        + entityClassName;

                String fullQualifiedDaoClassName = daoPackageName + "."
                        + entityClassName + daoClassPostfix;

                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                        .rootBeanDefinition(GenericDaoFactoryBean.class);
                beanDefinitionBuilder.addPropertyValue("daoInterface",
                        fullQualifiedDaoClassName);
                beanDefinitionBuilder.addPropertyValue("domainClass",
                        fullQualifiedEntityClassName);

                if ("" != daoBaseClassName) {
                    beanDefinitionBuilder.addPropertyValue("daoClass",
                            daoBaseClassName);
                }

                registry.registerBeanDefinition(name + daoNamePostfix,
                        beanDefinitionBuilder.getBeanDefinition());

            }
        }

        // Create PersistenceAnnotationPostProcessor definition
        if (!registry.containsBeanDefinition(PAB_POST_PROCESSOR.getName())) {

            BeanDefinition definition = BeanDefinitionBuilder
                    .rootBeanDefinition(PAB_POST_PROCESSOR).getBeanDefinition();

            registry.registerBeanDefinition(definition.getBeanClassName(),
                    definition);
        }

        // Create PersistenceExceptionTranslationPostProcessor definition
        if (!registry.containsBeanDefinition(PET_POST_PROCESSOR.getName())) {

            BeanDefinition definition = BeanDefinitionBuilder
                    .rootBeanDefinition(
                            PersistenceExceptionTranslationPostProcessor.class)
                    .getBeanDefinition();

            registry.registerBeanDefinition(definition.getBeanClassName(),
                    definition);
        }

        return null;
    }
}
