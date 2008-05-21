package org.synyx.hades.dao.config;

import java.io.IOException;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.synyx.hades.dao.orm.support.GenericDaoFactoryBean;
import org.synyx.hades.domain.Persistable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Parser to create bean definitions for dao-config namespace. Registers bean
 * definitions for DAOs as well as
 * {@code PersistenceExceptionTranslationPostProcessor} and
 * {@code PersistenceExceptionTranslationPostProcessor} to transparently inject
 * entity manager factory instance and apply exception translation.
 * <p>
 * The definition parser allows two ways of configuration. Either it looks up
 * the manually defined DAO instances or scans the defined domain package for
 * candidates for DAOs.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @author Eberhard Wolff
 */
public class DaoConfigDefinitionParser implements BeanDefinitionParser {

    private static final Log log = LogFactory
            .getLog(DaoConfigDefinitionParser.class);

    private static final Class<?> PAB_POST_PROCESSOR = PersistenceAnnotationBeanPostProcessor.class;
    private static final Class<?> PET_POST_PROCESSOR = PersistenceExceptionTranslationPostProcessor.class;


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element,
     *      org.springframework.beans.factory.xml.ParserContext)
     */
    public BeanDefinition parse(final Element element,
            final ParserContext parserContext) {

        XmlDaoConfigContext configContext = new XmlDaoConfigContext(element);
        BeanDefinitionRegistry registry = parserContext.getRegistry();

        if (configContext.configureManually()) {
            doManualConfiguration(configContext, registry);
        } else {
            doAutoConfiguration(configContext, registry);
        }

        registerPostProcessors(registry);

        return null;
    }


    /**
     * Executes DAO auto configuration by scanning the provided entity package
     * for classes implementing {@code Persistable}.
     * 
     * @param configContext
     * @param registry
     */
    private void doAutoConfiguration(final DaoConfigContext configContext,
            final BeanDefinitionRegistry registry) {

        if (log.isDebugEnabled()) {
            log.debug("Triggering auto DAO detection");
        }

        // Create scanner and apply filter
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        provider.addIncludeFilter(new PersistableTypeFilter());

        Set<BeanDefinition> beanDefinitions = provider
                .findCandidateComponents(configContext.getEntityPackageName());

        for (BeanDefinition definition : beanDefinitions) {

            // Retrieve bean name by altering first letter of the bean class
            // name to lowercase
            String beanName = StringUtils.uncapitalize(ClassUtils
                    .getShortName(definition.getBeanClassName()));

            registerGenericDaoFactoryBean(registry, beanName, configContext);
        }
    }


    /**
     * Proceeds manual configuration by traversing child elements.
     * 
     * @param context
     * @param registry
     */
    private void doManualConfiguration(final XmlDaoConfigContext context,
            final BeanDefinitionRegistry registry) {

        if (log.isDebugEnabled()) {
            log.debug("Triggering manual DAO detection");
        }

        NodeList childNodes = context.getChildNodes();

        // Add dao declarations
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {

                Element childElement = (Element) childNode;
                String name = childElement.getAttribute("name");

                registerGenericDaoFactoryBean(registry, name, context);
            }
        }
    }


    /**
     * Registers a {@code GenericDaoFactoryBean} for a bean with the given name
     * and the provided configuration context. It is mainly used to construct
     * bean name, entity class name and DAO interface name.
     * 
     * @param registry
     * @param name
     * @param context
     */
    private void registerGenericDaoFactoryBean(
            final BeanDefinitionRegistry registry, final String name,
            final DaoConfigContext context) {

        String entityClassName = StringUtils.capitalize(name);

        String domainClass = context.getEntityPackageName() + "."
                + entityClassName;

        String daoInterface = context.getDaoPackageName() + "."
                + entityClassName + context.getDaoClassPostfix();

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(GenericDaoFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("daoInterface", daoInterface);
        beanDefinitionBuilder.addPropertyValue("domainClass", domainClass);

        if (StringUtils.hasText(context.getDaoBaseClassName())) {
            beanDefinitionBuilder.addPropertyValue("daoClass", context
                    .getDaoBaseClassName());
        }

        if (log.isDebugEnabled()) {
            log
                    .debug("Registering Hades DAO: "
                            + name
                            + context.getDaoNamePostfix()
                            + " - DAO interface: "
                            + daoInterface
                            + " - Implementation base class: "
                            + (StringUtils.hasText(context
                                    .getDaoBaseClassName()) ? context
                                    .getDaoBaseClassName()
                                    : GenericDaoFactoryBean.DEFAULT_DAO_IMPLEMENTATION));
        }

        registry.registerBeanDefinition(name + context.getDaoNamePostfix(),
                beanDefinitionBuilder.getBeanDefinition());
    }


    /**
     * Registers necessary (Bean)PostProcessor instances if they have not
     * already been registered.
     * 
     * @param registry
     */
    private void registerPostProcessors(final BeanDefinitionRegistry registry) {

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
    }

    /**
     * {@code TypeFilter} implementation that detects classes that implement
     * {@code Persistable} and are annotated with {@link Entity}.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    static class PersistableTypeFilter implements TypeFilter {

        private AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(
                Entity.class);
        private AssignableTypeFilter assignableTypeFilter = new AssignableTypeFilter(
                Persistable.class);


        /*
         * (non-Javadoc)
         * 
         * @see org.springframework.core.type.filter.TypeFilter#match(org.springframework.core.type.classreading.MetadataReader,
         *      org.springframework.core.type.classreading.MetadataReaderFactory)
         */
        public boolean match(final MetadataReader metadataReader,
                final MetadataReaderFactory metadataReaderFactory)
                throws IOException {

            // Matches on correct type AND annotation
            return assignableTypeFilter.match(metadataReader,
                    metadataReaderFactory)
                    && annotationTypeFilter.match(metadataReader,
                            metadataReaderFactory);
        }
    }
}
