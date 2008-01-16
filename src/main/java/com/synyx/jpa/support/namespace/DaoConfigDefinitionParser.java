package com.synyx.jpa.support.namespace;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.synyx.jpa.support.GenericDaoFactoryBean;


/**
 * Parser to create bean definitions for dao-config namespace. Registers bean
 * definitions for DAOs as well as
 * <code>PersistenceExceptionTranslationPostProcessor</code> and
 * <code>PersistenceExceptionTranslationPostProcessor</code> to transparently
 * inject entity manager factory instance and apply exception translation.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public class DaoConfigDefinitionParser implements BeanDefinitionParser {

    private static final String DEFAULT_DAO_POSTFIX = "Dao";


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

        // Set default postfix if none configured
        daoClassPostfix = (null == daoClassPostfix) ? DEFAULT_DAO_POSTFIX
                : daoClassPostfix;

        NodeList childNodes = element.getChildNodes();

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
                beanDefinitionBuilder.addPropertyValue("entityClass",
                        fullQualifiedEntityClassName);

                parserContext.getRegistry().registerBeanDefinition(
                        name + daoNamePostfix,
                        beanDefinitionBuilder.getBeanDefinition());

            }
        }

        // Create PersistenceAnnotationPostProcessor definition
        BeanDefinition definition = BeanDefinitionBuilder.rootBeanDefinition(
                PersistenceAnnotationBeanPostProcessor.class)
                .getBeanDefinition();

        parserContext.getRegistry().registerBeanDefinition(
                definition.getBeanClassName(), definition);

        // Create PersistenceExceptionTranslationPostProcessor definition
        definition = BeanDefinitionBuilder.rootBeanDefinition(
                PersistenceExceptionTranslationPostProcessor.class)
                .getBeanDefinition();

        parserContext.getRegistry().registerBeanDefinition(
                definition.getBeanClassName(), definition);

        return null;
    }
}
